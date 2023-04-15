package com.foodorder.service.order.domain.port;

import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMsgPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderRequestHandler {

    private final CreateOrderHelper helper;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedPaymentRequestMsgPublisher orderCreatedPaymentRequestMsgPublisher;

    public CreateOrderRequestHandler(CreateOrderHelper helper, OrderDataMapper orderDataMapper,
                                     OrderCreatedPaymentRequestMsgPublisher orderCreatedPaymentRequestMsgPublisher) {
        this.helper = helper;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedPaymentRequestMsgPublisher = orderCreatedPaymentRequestMsgPublisher;
    }

    /**
     * Note that we cannot have @Transactional annotation here as the method contains two operations over
     * two different data sources.
     */
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        OrderCreatedEvent orderCreatedEvent = helper.validateAndPersistOrder(requestDTO);
        log.info("Order is initiated with id: {}", orderCreatedEvent.getOrder().getId().getIdValue());
        // publish event, ensure atomicity using Outbox pattern
        orderCreatedPaymentRequestMsgPublisher.publish(orderCreatedEvent);
        return orderDataMapper.fromOrderToResponseDTO(orderCreatedEvent.getOrder());
    }

}
