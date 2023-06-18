package com.foodorder.service.order.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCreatedEvent, Order> orderCreatedEventPublisher;

    public OrderCreatedEvent(Order order,
                             ZonedDateTime createdAt,
                             DomainEventPublisher<OrderCreatedEvent, Order> orderCreatedEventPublisher) {
        super(order, createdAt);
        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
    }

    @Override
    public void fire() {
        orderCreatedEventPublisher.publish(this);
    }
}
