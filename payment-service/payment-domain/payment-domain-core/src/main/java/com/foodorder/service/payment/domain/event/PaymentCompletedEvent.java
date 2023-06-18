package com.foodorder.service.payment.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCompletedEvent, Payment> paymentCompletedEventPublisher;

    public PaymentCompletedEvent(Payment payment,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCompletedEvent, Payment> paymentCompletedEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedEventPublisher = paymentCompletedEventPublisher;
    }

    @Override
    public void fire() {
        paymentCompletedEventPublisher.publish(this);
    }
}
