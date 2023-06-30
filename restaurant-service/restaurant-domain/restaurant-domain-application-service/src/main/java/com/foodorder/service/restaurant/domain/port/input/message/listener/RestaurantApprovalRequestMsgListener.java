package com.foodorder.service.restaurant.domain.port.input.message.listener;

import com.foodorder.service.restaurant.domain.dto.RestaurantApprovalRequestDTO;

public interface RestaurantApprovalRequestMsgListener {

    void approveOrder(RestaurantApprovalRequestDTO requestDTO);

}
