package com.foodorder.service.payment.domain.port.output.message.publisher;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMsgPublisher extends DomainEventPublisher<PaymentCancelledEvent, Payment> {

}
