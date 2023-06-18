package com.foodorder.service.order.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelInitiatedEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCancelInitiatedEvent, Order> orderCancelInitiatedEventPublisher;

    public OrderCancelInitiatedEvent(Order order,
                                     ZonedDateTime createdAt,
                                     DomainEventPublisher<OrderCancelInitiatedEvent, Order> orderCancelInitiatedEventPublisher) {
        super(order, createdAt);
        this.orderCancelInitiatedEventPublisher = orderCancelInitiatedEventPublisher;
    }

    @Override
    public void fire() {
        orderCancelInitiatedEventPublisher.publish(this);
    }
}
