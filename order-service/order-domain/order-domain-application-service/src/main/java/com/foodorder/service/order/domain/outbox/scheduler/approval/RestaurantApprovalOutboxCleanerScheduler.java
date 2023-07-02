package com.foodorder.service.order.domain.outbox.scheduler.approval;

import com.foodorder.outbox.OutboxScheduler;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;

    public RestaurantApprovalOutboxCleanerScheduler(ApprovalOutboxHelper approvalOutboxHelper) {
        this.approvalOutboxHelper = approvalOutboxHelper;
    }

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        var outboxMessagesOpt = approvalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatuses(
                OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
        );
        if (outboxMessagesOpt.isPresent()) {
            var outboxMessages = outboxMessagesOpt.get();
            log.info("Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
                    outboxMessages.size(),
                    outboxMessages.stream().map(OrderApprovalOutboxMessage::getPayload).collect(Collectors.joining("\n")));
            approvalOutboxHelper.deleteApprovalOutboxMessagesByOutboxStatusAndSagaStatuses(
                    OutboxStatus.COMPLETED,
                    SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED
            );
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
        }

    }
}
