package com.foodorder.service.order.domain.event;

import com.foodorder.service.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelInitiatedEvent extends OrderEvent {

    public OrderCancelInitiatedEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
