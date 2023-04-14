package com.foodorder.service.order.domain.port.output.repository;

import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
