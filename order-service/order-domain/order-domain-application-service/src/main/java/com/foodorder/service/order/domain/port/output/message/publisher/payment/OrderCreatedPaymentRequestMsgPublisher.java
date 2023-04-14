package com.foodorder.service.order.domain.port.output.message.publisher.payment;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMsgPublisher extends DomainEventPublisher<OrderCreatedEvent, Order> {
    //
}
