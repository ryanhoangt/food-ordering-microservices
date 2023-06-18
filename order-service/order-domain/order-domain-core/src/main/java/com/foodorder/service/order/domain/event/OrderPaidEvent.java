package com.foodorder.service.order.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    private final DomainEventPublisher<OrderPaidEvent, Order> orderPaidEventPublisher;

    public OrderPaidEvent(Order order,
                          ZonedDateTime createdAt,
                          DomainEventPublisher<OrderPaidEvent, Order> orderPaidEventPublisher) {
        super(order, createdAt);
        this.orderPaidEventPublisher = orderPaidEventPublisher;
    }

    @Override
    public void fire() {
        orderPaidEventPublisher.publish(this);
    }
}
