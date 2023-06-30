package com.foodorder.service.restaurant.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderRejectedEvent, OrderApproval> orderRejectedEventPublisher;

    public OrderRejectedEvent(OrderApproval orderApproval,
                              RestaurantId restaurantId,
                              List<String> failureMessages,
                              ZonedDateTime createdAt,
                              DomainEventPublisher<OrderRejectedEvent, OrderApproval> orderRejectedEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderRejectedEventPublisher = orderRejectedEventPublisher;
    }

    @Override
    public void fire() {

    }

}
