package com.foodorder.service.order.dataaccess.order.adapter;

import com.foodorder.service.order.dataaccess.order.mapper.OrderDataAccessMapper;
import com.foodorder.service.order.dataaccess.order.repository.OrderJpaRepository;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import com.foodorder.service.order.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.fromOrderEntityToOrder(
                orderJpaRepository.save(orderDataAccessMapper.fromOrderToOrderEntity(order))
        );
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getIdValue())
                .map(orderDataAccessMapper::fromOrderEntityToOrder);
    }
}
