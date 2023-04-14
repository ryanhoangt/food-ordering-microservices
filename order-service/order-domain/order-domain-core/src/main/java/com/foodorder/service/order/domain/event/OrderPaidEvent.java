package com.foodorder.service.order.domain.event;

import com.foodorder.domain.event.DomainEvent;
import com.foodorder.service.order.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
