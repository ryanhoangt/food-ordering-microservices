package com.foodorder.service.order.domain.port.output.message.publisher.restaurant;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMsgPublisher extends DomainEventPublisher<OrderPaidEvent, Order> {
    //
}
