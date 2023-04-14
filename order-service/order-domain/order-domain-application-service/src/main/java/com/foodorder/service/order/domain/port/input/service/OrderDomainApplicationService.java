package com.foodorder.service.order.domain.port.input.service;

import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderRequestDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderResponseDTO;

import javax.validation.Valid;

/**
 * Note that @Valid annotation must be placed in the specification, not in the
 * implementations.
 */
public interface OrderDomainApplicationService {

    CreateOrderResponseDTO createOrder(@Valid CreateOrderRequestDTO requestDTO);

    TrackOrderResponseDTO trackOrder(@Valid TrackOrderRequestDTO requestDTO);
}
