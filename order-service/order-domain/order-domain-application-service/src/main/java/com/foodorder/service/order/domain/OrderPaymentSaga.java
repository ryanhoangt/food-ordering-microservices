package com.foodorder.service.order.domain;

import com.foodorder.domain.event.EmptyEvent;
import com.foodorder.saga.SagaStep;
import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.port.output.message.publisher.restaurant.OrderPaidRestaurantRequestMsgPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponseDTO, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderPaidRestaurantRequestMsgPublisher orderPaidRestaurantRequestMsgPublisher;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderSagaHelper orderSagaHelper,
                            OrderPaidRestaurantRequestMsgPublisher orderPaidRestaurantRequestMsgPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.orderPaidRestaurantRequestMsgPublisher = orderPaidRestaurantRequestMsgPublisher;
    }

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponseDTO responseDTO) {
        log.info("Completing payment for orderwith id: {}", responseDTO.getOrderId());
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        OrderPaidEvent domainEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMsgPublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is paid", order.getId().getIdValue());
        return domainEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponseDTO responseDTO) {
        log.info("Cancelling order with id: {}", responseDTO);
        Order order = orderSagaHelper.findOrder(responseDTO.getOrderId());
        orderDomainService.cancelOrder(order, responseDTO.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelled.", order.getId().getIdValue());
        return EmptyEvent.INSTANCE;
    }
}
