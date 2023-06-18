package com.foodorder.service.order.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.event.OrderPaidEvent;

import java.util.List;

/**
 * The interface for the Domain Service class sitting in front of
 * all the domain entities.
 * Note that an approach of delegating the firing of an event to the
 * caller (ApplicationService in this case) is used here.
 */
public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent, Order> orderCreatedEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent, Order> orderPaidEventPublisher);

    void approveOrder(Order order);

    OrderCancelInitiatedEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelInitiatedEvent, Order> orderCancelInitiatedEventPublisher);

    void cancelOrder(Order order, List<String> failureMessages);
}
