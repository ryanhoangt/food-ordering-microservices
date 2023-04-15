package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;
import com.foodorder.service.order.domain.port.input.message.listener.restaurant.RestaurantResponseMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class RestaurantResponseMsgListenerImpl implements RestaurantResponseMsgListener {

    @Override
    public void orderApprovedHandler(RestaurantResponseDTO responseDTO) {

    }

    @Override
    public void orderRejectedHandler(RestaurantResponseDTO responseDTO) {

    }
}
