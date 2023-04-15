package com.foodorder.service.order.domain.port;

import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderRequestDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderResponseDTO;
import com.foodorder.service.order.domain.port.input.service.OrderDomainApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class OrderDomainApplicationServiceImpl implements OrderDomainApplicationService {

    private final CreateOrderRequestHandler createOrderRequestHandler;
    private final TrackOrderRequestHandler trackOrderRequestHandler;

    public OrderDomainApplicationServiceImpl(CreateOrderRequestHandler createOrderRequestHandler, TrackOrderRequestHandler trackOrderRequestHandler) {
        this.createOrderRequestHandler = createOrderRequestHandler;
        this.trackOrderRequestHandler = trackOrderRequestHandler;
    }

    @Override
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        return createOrderRequestHandler.createOrder(requestDTO);
    }

    @Override
    public TrackOrderResponseDTO trackOrder(TrackOrderRequestDTO requestDTO) {
        return trackOrderRequestHandler.trackOrder(requestDTO);
    }
}
