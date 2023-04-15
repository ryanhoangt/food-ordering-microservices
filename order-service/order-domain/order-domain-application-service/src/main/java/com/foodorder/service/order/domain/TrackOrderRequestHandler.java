package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.dto.track.TrackOrderRequestDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.exception.OrderNotFoundException;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import com.foodorder.service.order.domain.valueobject.TrackingId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class TrackOrderRequestHandler {

    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    public TrackOrderRequestHandler(OrderRepository orderRepository, OrderDataMapper orderDataMapper) {
        this.orderRepository = orderRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponseDTO trackOrder(TrackOrderRequestDTO requestDTO) {
        Optional<Order> orderOpt = orderRepository.findByTrackingId(new TrackingId(requestDTO.getOrderTrackingId()));
        if (orderOpt.isEmpty()) {
            log.warn("Could not find order with tracking id: {}", requestDTO.getOrderTrackingId());
            throw new OrderNotFoundException("Could not find order with tracking id: " + requestDTO.getOrderTrackingId());
        }
        return orderDataMapper.fromOrderToTrackOrderResponseDTO(orderOpt.get());
    }
}