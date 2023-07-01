package com.foodorder.service.order.domain.port.output.message.publisher.payment;

import com.foodorder.outbox.OutboxStatus;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentRequestOuboxMsgPublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback); // for updating the outbox table
}
