package com.foodorder.service.order.domain;

import com.foodorder.domain.event.EmptyEvent;
import com.foodorder.saga.SagaStep;
import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMsgPublisher;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantResponseDTO, EmptyEvent, OrderCancelInitiatedEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMsgPublisher orderCancelledPaymentRequestMsgPublisher;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderSagaHelper orderSagaHelper,
                             OrderCancelledPaymentRequestMsgPublisher orderCancelledPaymentRequestMsgPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.orderCancelledPaymentRequestMsgPublisher = orderCancelledPaymentRequestMsgPublisher;
    }

    @Override
    @Transactional
    public EmptyEvent process(RestaurantResponseDTO responseDTO) {
        log.info("Approving order with id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is approved!", order.getId().getIdValue());
        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelInitiatedEvent rollback(RestaurantResponseDTO responseDTO) {
        log.info("Cancelling order with id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        OrderCancelInitiatedEvent domainEvent = orderDomainService.cancelOrderPayment(
                order, responseDTO.getFailureMessages(), orderCancelledPaymentRequestMsgPublisher
        );
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelling.", order.getId().getIdValue());
        return domainEvent;
    }
}
