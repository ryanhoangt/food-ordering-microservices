package com.foodorder.service.order.domain.outbox.scheduler.payment;

import com.foodorder.outbox.OutboxScheduler;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.PaymentRequestOutboxMsgPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestOutboxMsgPublisher paymentRequestOutboxMsgPublisher;

    public PaymentOutboxScheduler(PaymentOutboxHelper paymentOutboxHelper,
                                  PaymentRequestOutboxMsgPublisher paymentRequestOutboxMsgPublisher) {
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.paymentRequestOutboxMsgPublisher = paymentRequestOutboxMsgPublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
                initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var outboxMessagesOpt = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatuses(
                OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING
        );

        if (outboxMessagesOpt.isPresent() && outboxMessagesOpt.get().size() > 0) {
            var outboxMessages = outboxMessagesOpt.get();
            log.info("Received {} OrderPaymentOutboxMessage with ids: {}, sending to messaging bus...",
                    outboxMessages.size(), outboxMessages.stream()
                            .map(OrderPaymentOutboxMessage::getId)
                            .map(UUID::toString)
                            .collect(Collectors.joining(",")));

            outboxMessages.forEach(outboxMessage ->
                    paymentRequestOutboxMsgPublisher.publish(outboxMessage, this::updateOutboxStatus));
            log.info("{} OrderPaymentOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(orderPaymentOutboxMessage);
        log.info("OrderPaymentOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }
}
