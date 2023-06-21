package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.port.input.message.listener.restaurant.RestaurantResponseMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.foodorder.service.order.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class RestaurantResponseMsgListenerImpl implements RestaurantResponseMsgListener {

    private final OrderApprovalSaga orderApprovalSaga;

    public RestaurantResponseMsgListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }

    @Override
    public void orderApprovedHandler(RestaurantResponseDTO responseDTO) {
        orderApprovalSaga.process(responseDTO);
        log.info("Order is approved for order id: {}", responseDTO.getOrderId());
    }

    @Override
    public void orderRejectedHandler(RestaurantResponseDTO responseDTO) {
        OrderCancelInitiatedEvent domainEvent = orderApprovalSaga.rollback(responseDTO);
        log.info("Publishing order cancelled event for order id: {} with failure messages: {}",
                responseDTO.getOrderId(), String.join(FAILURE_MESSAGE_DELIMITER, responseDTO.getFailureMessages()));
        domainEvent.fire();
    }
}
