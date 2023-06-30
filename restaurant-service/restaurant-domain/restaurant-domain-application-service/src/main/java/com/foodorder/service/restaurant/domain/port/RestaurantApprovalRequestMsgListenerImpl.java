package com.foodorder.service.restaurant.domain.port;

import com.foodorder.service.restaurant.domain.dto.RestaurantApprovalRequestDTO;
import com.foodorder.service.restaurant.domain.event.OrderApprovalEvent;
import com.foodorder.service.restaurant.domain.port.input.message.listener.RestaurantApprovalRequestMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestaurantApprovalRequestMsgListenerImpl implements RestaurantApprovalRequestMsgListener {

    private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    public RestaurantApprovalRequestMsgListenerImpl(RestaurantApprovalRequestHelper restaurantApprovalRequestHelper) {
        this.restaurantApprovalRequestHelper = restaurantApprovalRequestHelper;
    }

    @Override
    public void approveOrder(RestaurantApprovalRequestDTO requestDTO) {
        OrderApprovalEvent orderApprovalEvent = restaurantApprovalRequestHelper.persistOrderApproval(requestDTO);
        orderApprovalEvent.fire();
    }
}
