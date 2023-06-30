package com.foodorder.service.restaurant.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderApprovedEvent, OrderApproval> orderApprovedEventPublisher;

    public OrderApprovedEvent(OrderApproval orderApproval,
                              RestaurantId restaurantId,
                              List<String> failureMessages,
                              ZonedDateTime createdAt,
                              DomainEventPublisher<OrderApprovedEvent, OrderApproval> orderApprovedEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderApprovedEventPublisher = orderApprovedEventPublisher;
    }

    @Override
    public void fire() {

    }
}
