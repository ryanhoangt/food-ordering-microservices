package com.foodorder.service.restaurant.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import com.foodorder.service.restaurant.domain.event.OrderApprovalEvent;
import com.foodorder.service.restaurant.domain.event.OrderApprovedEvent;
import com.foodorder.service.restaurant.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages,
                                     DomainEventPublisher<OrderApprovedEvent, OrderApproval> orderApprovedEventPublisher,
                                     DomainEventPublisher<OrderRejectedEvent, OrderApproval> orderRejectedEventPublisher);
}
