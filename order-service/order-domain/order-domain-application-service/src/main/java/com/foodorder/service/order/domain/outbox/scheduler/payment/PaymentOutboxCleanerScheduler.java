package com.foodorder.service.order.domain.outbox.scheduler.payment;

import com.foodorder.outbox.OutboxScheduler;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;

    public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        var outboxMessagesOpt = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatuses(
                OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED
        );

        if (outboxMessagesOpt.isPresent()) {
            var outboxMessages = outboxMessagesOpt.get();
            log.info("Received {} OrderPaymentOutboxMessage for clean-up. Payloads: {}", outboxMessages.size(),
                    outboxMessages.stream().map(OrderPaymentOutboxMessage::getPayload).collect(Collectors.joining("\n")));

            paymentOutboxHelper.deletePaymentOutboxMessagesByOutboxStatusAndSagaStatuses(
                    OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED, SagaStatus.FAILED
            );
            log.info("{} OrderPaymentOutboxMessage deleted!", outboxMessages.size());
        }
    }
}
