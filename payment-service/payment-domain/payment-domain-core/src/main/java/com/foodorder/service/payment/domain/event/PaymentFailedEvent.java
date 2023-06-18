package com.foodorder.service.payment.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher;

    public PaymentFailedEvent(Payment payment,
                              ZonedDateTime createdAt,
                              List<String> failureMessages,
                              DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher) {
        super(payment, createdAt, failureMessages);
        this.paymentFailedEventPublisher = paymentFailedEventPublisher;
    }

    @Override
    public void fire() {
        paymentFailedEventPublisher.publish(this);
    }
}
