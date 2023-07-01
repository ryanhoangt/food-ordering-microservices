package com.foodorder.service.order.domain.port.output.message.publisher.restaurant;

import com.foodorder.outbox.OutboxStatus;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantRequestOutboxMsgPublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
