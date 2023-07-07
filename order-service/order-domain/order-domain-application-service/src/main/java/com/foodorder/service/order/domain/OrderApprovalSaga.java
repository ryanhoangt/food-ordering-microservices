package com.foodorder.service.order.domain;

import com.foodorder.domain.valueobject.OrderStatus;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.saga.SagaStep;
import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.foodorder.service.order.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.foodorder.service.order.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.foodorder.domain.DomainConstants.UTC_ZONE_ID;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantResponseDTO> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderSagaHelper orderSagaHelper,
                             PaymentOutboxHelper paymentOutboxHelper,
                             ApprovalOutboxHelper approvalOutboxHelper,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(RestaurantResponseDTO responseDTO) {
        var outboxMessageOpt = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(responseDTO.getSagaId()),
                SagaStatus.PROCESSING
        );
        if (outboxMessageOpt.isEmpty()) {
            log.info("An outbox with saga id: {} is already processed!", responseDTO.getSagaId());
            return;
        }
        var outboxMessage = outboxMessageOpt.get();

        Order order = approveOrder(responseDTO);
        SagaStatus sagaStatus = orderSagaHelper.fromOrderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                outboxMessage, order.getOrderStatus(), sagaStatus));
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
                responseDTO.getSagaId(), order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is approved!", order.getId().getIdValue());
    }

    @Override
    @Transactional
    public void rollback(RestaurantResponseDTO responseDTO) {
        var outboxMessageOpt = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(responseDTO.getSagaId()),
                SagaStatus.PROCESSING
        );
        if (outboxMessageOpt.isEmpty()) {
            log.info("An outbox with saga id: {} is already roll backed!", responseDTO.getSagaId());
            return;
        }
        var outboxMessage = outboxMessageOpt.get();

        OrderCancelInitiatedEvent domainEvent = rollbackOrder(responseDTO);
        SagaStatus sagaStatus = orderSagaHelper.fromOrderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                outboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus
        ));
        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.fromOrderCancelInitiatedEventToOrderPaymentEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(responseDTO.getSagaId())
        );

        log.info("Order with id: {} is cancelling.", domainEvent.getOrder().getId().getIdValue());
    }

    private Order approveOrder(RestaurantResponseDTO responseDTO) {
        log.info("Approving order with id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage outboxMessage,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        var outboxMessageOpt = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId), SagaStatus.PROCESSING
        );
        if (outboxMessageOpt.isEmpty()) {
            throw new OrderDomainException("Payment outbox message cannot be found in " +
                    SagaStatus.PROCESSING.name() + " state");
        }
        OrderPaymentOutboxMessage outboxMessage = outboxMessageOpt.get();
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderCancelInitiatedEvent rollbackOrder(RestaurantResponseDTO responseDTO) {
        log.info("Cancelling order with id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        OrderCancelInitiatedEvent domainEvent = orderDomainService.cancelOrderPayment(
                order, responseDTO.getFailureMessages()
        );
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }
}
