package com.foodorder.service.payment.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.PaymentStatus;
import com.foodorder.service.payment.domain.entity.CreditEntry;
import com.foodorder.service.payment.domain.entity.CreditHistory;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.event.PaymentCancelledEvent;
import com.foodorder.service.payment.domain.event.PaymentCompletedEvent;
import com.foodorder.service.payment.domain.event.PaymentEvent;
import com.foodorder.service.payment.domain.event.PaymentFailedEvent;
import com.foodorder.service.payment.domain.valueobject.CreditHistoryId;
import com.foodorder.service.payment.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.foodorder.domain.DomainConstants.UTC_ZONE_ID;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment,
                                                   CreditEntry creditEntry,
                                                   List<CreditHistory> creditHistories,
                                                   List<String> failureMessages,
                                                   DomainEventPublisher<PaymentCompletedEvent, Payment> paymentCompletedEventPublisher,
                                                   DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id: {}", payment.getOrderId().getIdValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), paymentCompletedEventPublisher);
        } else {
            log.info("Payment initiation is failed for order id: {}", payment.getOrderId().getIdValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), failureMessages, paymentFailedEventPublisher);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistories,
                                                 List<String> failureMessages,
                                                 DomainEventPublisher<PaymentCancelledEvent, Payment> paymentCancelledEventPublisher,
                                                 DomainEventPublisher<PaymentFailedEvent, Payment> paymentFailedEventPublisher) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: {}", payment.getOrderId().getIdValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), paymentCancelledEventPublisher);
        } else {
            log.info("Payment cancellation is failed for order id: {}", payment.getOrderId().getIdValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)), failureMessages, paymentFailedEventPublisher);
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            String errorMsg = "Customer with id=" + payment.getCustomerId().getIdValue()
                    + " doesn't have enough credit for payment.";
            log.error(errorMsg);
            failureMessages.add(errorMsg);
        }
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(Payment payment,
                                     List<CreditHistory> creditHistories,
                                     TransactionType transactionType) {
        creditHistories.add(CreditHistory.Builder.builder()
                        .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                        .customerId(payment.getCustomerId())
                        .amount(payment.getPrice())
                        .transactionType(transactionType)
                        .build());
    }

    private void validateCreditHistory(CreditEntry creditEntry,
                                       List<CreditHistory> creditHistories,
                                       List<String> failureMessages) {
        Money totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            String errorMsg = "Customer with id=" + creditEntry.getCustomerId().getIdValue()
                    + " doesn't have enough credit according to credit history.";
            log.error(errorMsg);
            failureMessages.add(errorMsg);
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            String errorMsg = "Credit history total is not equal to current credit of customer id="
                    + creditEntry.getCustomerId().getIdValue() + ".";
            log.error(errorMsg);
            failureMessages.add(errorMsg);
        }
    }

    private static Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> creditHistory.getTransactionType() == transactionType)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }
}
