package com.foodorder.service.payment.domain.event;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCancelledEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCancelledEvent, Payment> paymentCancelledEventPublisher;

    public PaymentCancelledEvent(Payment payment,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCancelledEvent, Payment> paymentCancelledEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCancelledEventPublisher = paymentCancelledEventPublisher;
    }

    @Override
    public void fire() {
         paymentCancelledEventPublisher.publish(this);
    }
}
