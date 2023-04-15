package com.foodorder.service.order.domain.port;

import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMsgPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderCreatedEventApplicationListener {

    private final OrderCreatedPaymentRequestMsgPublisher msgPublisher;

    public OrderCreatedEventApplicationListener(OrderCreatedPaymentRequestMsgPublisher msgPublisher) {
        this.msgPublisher = msgPublisher;
    }

    @TransactionalEventListener
    void process(OrderCreatedEvent orderCreatedEvent) {
        msgPublisher.publish(orderCreatedEvent);
    }
}
