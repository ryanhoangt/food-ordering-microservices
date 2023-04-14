package com.foodorder.service.order.domain.port.input.message.listener.restaurant;

import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;

public interface RestaurantResponseMsgListener {

    void orderApprovedHandler(RestaurantResponseDTO responseDTO);

    void orderRejectedHandler(RestaurantResponseDTO responseDTO);
}
