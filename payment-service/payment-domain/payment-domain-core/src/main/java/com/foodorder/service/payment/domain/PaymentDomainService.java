package com.foodorder.service.payment.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.payment.domain.entity.CreditEntry;
import com.foodorder.service.payment.domain.entity.CreditHistory;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.event.PaymentCancelledEvent;
import com.foodorder.service.payment.domain.event.PaymentCompletedEvent;
import com.foodorder.service.payment.domain.event.PaymentEvent;
import com.foodorder.service.payment.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent, Payment> paymentCompletedEventPublisher, DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages, DomainEventPublisher<PaymentCancelledEvent, Payment> paymentCancelledEventPublisher, DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher);


}
