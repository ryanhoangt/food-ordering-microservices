package com.foodorder.service.payment.domain.port.output.message.publisher;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedMsgPublisher extends DomainEventPublisher<PaymentCompletedEvent, Payment> {

}
