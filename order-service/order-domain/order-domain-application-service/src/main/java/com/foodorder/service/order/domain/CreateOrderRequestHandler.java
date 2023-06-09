package com.foodorder.service.order.domain;

import com.foodorder.outbox.OutboxStatus;
import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class CreateOrderRequestHandler {

    private final CreateOrderHelper createOrderHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    public CreateOrderRequestHandler(CreateOrderHelper createOrderHelper,
                                     OrderDataMapper orderDataMapper,
                                     PaymentOutboxHelper paymentOutboxHelper,
                                     OrderSagaHelper orderSagaHelper) {
        this.createOrderHelper = createOrderHelper;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
    }

    /**
     * Note that we cannot have @Transactional annotation here as the method contains two operations over
     * two different data sources.
     */
    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        OrderCreatedEvent orderCreatedEvent = createOrderHelper.validateAndPersistOrder(requestDTO);
        log.info("Order is initiated with id: {}", orderCreatedEvent.getOrder().getId().getIdValue());
        // publish event, ensure atomicity using Outbox pattern
        CreateOrderResponseDTO responseDTO = orderDataMapper.fromOrderToCreateOrderResponseDTO(orderCreatedEvent.getOrder(), "Order created successfully");
        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.fromOrderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.fromOrderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );
        log.info("Returning CreateOrderResponseDTO with order id: {}", orderCreatedEvent.getOrder().getId().getIdValue());
        return responseDTO;
    }

}