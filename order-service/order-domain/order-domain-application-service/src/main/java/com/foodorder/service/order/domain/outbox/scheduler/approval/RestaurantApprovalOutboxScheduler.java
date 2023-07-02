package com.foodorder.service.order.domain.outbox.scheduler.approval;

import com.foodorder.outbox.OutboxScheduler;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.foodorder.service.order.domain.port.output.message.publisher.restaurant.RestaurantRequestOutboxMsgPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final RestaurantRequestOutboxMsgPublisher restaurantRequestOutboxMsgPublisher;

    public RestaurantApprovalOutboxScheduler(ApprovalOutboxHelper approvalOutboxHelper,
                                             RestaurantRequestOutboxMsgPublisher restaurantRequestOutboxMsgPublisher) {
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.restaurantRequestOutboxMsgPublisher = restaurantRequestOutboxMsgPublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var outboxMessagesOpt = approvalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatuses(
                OutboxStatus.STARTED,
                SagaStatus.PROCESSING
        );
        if (outboxMessagesOpt.isPresent() && outboxMessagesOpt.get().size() > 0) {
            var outboxMessages = outboxMessagesOpt.get();
            log.info("Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus...",
                    outboxMessages.size(),
                    outboxMessages.stream().map(OrderApprovalOutboxMessage::getId).map(UUID::toString).collect(Collectors.joining(",")));

            outboxMessages.forEach(outboxMessage -> restaurantRequestOutboxMsgPublisher.publish(outboxMessage, this::updateOutboxStatus));
            log.info("{} OrderApprovalOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }

    private void updateOutboxStatus(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
        orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
        approvalOutboxHelper.save(orderApprovalOutboxMessage);
        log.info("OrderApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }
}
