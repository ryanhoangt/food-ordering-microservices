package com.foodorder.service.order.domain;

import com.foodorder.domain.valueobject.OrderStatus;
import com.foodorder.domain.valueobject.PaymentStatus;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.saga.SagaStep;
import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
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
import java.util.Optional;
import java.util.UUID;

import static com.foodorder.domain.DomainConstants.UTC_ZONE_ID;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponseDTO> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
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
    public void process(PaymentResponseDTO responseDTO) {
        Optional<OrderPaymentOutboxMessage> paymentOutboxMessageOpt = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(responseDTO.getSagaId()),
                SagaStatus.STARTED
        );
        if (paymentOutboxMessageOpt.isEmpty()) {
            /* An edge case when the same outbox message is sent twice to the Kafka topic,
             * due to schedulers/multiple server instances.
             */
            log.info("An outbox message with saga id: {} is already processed!", responseDTO.getSagaId());
            return;
        }
        OrderPaymentOutboxMessage paymentOutboxMessage = paymentOutboxMessageOpt.get();

        OrderPaidEvent domainEvent = completePaymentForOrder(responseDTO);

        SagaStatus sagaStatus = orderSagaHelper.fromOrderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOuboxMessage(
                paymentOutboxMessage,
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus));

        approvalOutboxHelper.saveApprovalOutboxMessage(orderDataMapper.fromOrderPaidEventToOrderApprovalEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(responseDTO.getSagaId()));

        log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getIdValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponseDTO responseDTO) {
        var paymentOutboxMessageOpt = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(responseDTO.getSagaId()),
                getCurrentSagaStatuses(responseDTO.getPaymentStatus())
        );

        if (paymentOutboxMessageOpt.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rolled back!", responseDTO.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage paymentOutboxMessage = paymentOutboxMessageOpt.get();
        Order order = rollbackPaymentForOrder(responseDTO);
        SagaStatus sagaStatus = orderSagaHelper.fromOrderStatusToSagaStatus(order.getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOuboxMessage(
                paymentOutboxMessage,
                order.getOrderStatus(),
                sagaStatus
        ));
        if (responseDTO.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                    responseDTO.getSagaId(),
                    order.getOrderStatus(),
                    sagaStatus));
        }
        log.info("Order with id: {} is cancelled.", order.getId().getIdValue());
    }


    private SagaStatus[] getCurrentSagaStatuses(PaymentStatus paymentStatus) {
        switch (paymentStatus) {
            case COMPLETED:
                return new SagaStatus[] { SagaStatus.STARTED };
            case CANCELLED:
                return new SagaStatus[] { SagaStatus.PROCESSING };
            case FAILED:
                return new SagaStatus[] { SagaStatus.STARTED, SagaStatus.PROCESSING };
        }
        return null; // Unreachable
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOuboxMessage(OrderPaymentOutboxMessage paymentOutboxMessage,
                                                                    OrderStatus orderStatus,
                                                                    SagaStatus sagaStatus) {
        paymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)));
        paymentOutboxMessage.setOrderStatus(orderStatus);
        paymentOutboxMessage.setSagaStatus(sagaStatus);
        return paymentOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponseDTO responseDTO) {
        log.info("Completing payment for order with id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }

    private Order rollbackPaymentForOrder(PaymentResponseDTO responseDTO) {
        log.info("Cancelling order with id: {}", responseDTO);
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        orderDomainService.cancelOrder(order, responseDTO.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        var approvalOutboxMessageOpt = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.COMPENSATING
        );
        if (approvalOutboxMessageOpt.isEmpty()) {
            throw new OrderDomainException("Approval outbox message could not be found in " +
                    SagaStatus.COMPENSATING.name() + " status");
        }
        OrderApprovalOutboxMessage approvalOutboxMessage = approvalOutboxMessageOpt.get();
        approvalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)));
        approvalOutboxMessage.setOrderStatus(orderStatus);
        approvalOutboxMessage.setSagaStatus(sagaStatus);
        return approvalOutboxMessage;
    }
}
