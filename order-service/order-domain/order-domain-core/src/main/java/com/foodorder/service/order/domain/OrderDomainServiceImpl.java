package com.foodorder.service.order.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.foodorder.domain.DomainConstants.UTC_ZONE_ID;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order,
                                                      Restaurant restaurant,
                                                      DomainEventPublisher<OrderCreatedEvent, Order> orderCreatedEventPublisher) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated.", order.getId().getIdValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), orderCreatedEventPublisher);
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive())
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getIdValue()
            + " is currently not active.");
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getItems().forEach(orderItem -> restaurant.getProducts().forEach(restProduct -> {
            Product orderProduct = orderItem.getProduct();
            if (orderProduct.equals(restProduct))
                orderProduct.updateWithConfirmedNameAndPrice(
                        restProduct.getName(),
                        restProduct.getPrice()
                );
        }));
    }

    @Override
    public OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent, Order> orderPaidEventPublisher) {
        order.pay();
        log.info("Order with id: {} is paid.", order.getId().getIdValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), orderPaidEventPublisher);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved.", order.getId().getIdValue());
    }

    @Override
    public OrderCancelInitiatedEvent cancelOrderPayment(Order order,
                                                        List<String> failureMessages,
                                                        DomainEventPublisher<OrderCancelInitiatedEvent, Order> orderCancelInitiatedEventPublisher) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order id: {}.", order.getId().getIdValue());
        return new OrderCancelInitiatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), orderCancelInitiatedEventPublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled.", order.getId().getIdValue());
    }
}
