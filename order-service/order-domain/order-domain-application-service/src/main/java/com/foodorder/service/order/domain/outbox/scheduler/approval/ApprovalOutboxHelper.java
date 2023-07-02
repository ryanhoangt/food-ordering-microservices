package com.foodorder.service.order.domain.outbox.scheduler.approval;

import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.foodorder.service.order.domain.port.output.repository.ApprovalOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.foodorder.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;

    public ApprovalOutboxHelper(ApprovalOutboxRepository approvalOutboxRepository) {
        this.approvalOutboxRepository = approvalOutboxRepository;
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessagesByOutboxStatusAndSagaStatuses(
            OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID sagaId,
                                                                                              SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, sagaStatuses);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage response = approvalOutboxRepository.save(orderApprovalOutboxMessage);

        if (response == null) {
            String errorMsg = "Could not save OrderApprovalOutboxMessage with outbox id: " + orderApprovalOutboxMessage.getId();
            log.error(errorMsg);
            throw new OrderDomainException(errorMsg);
        }

        log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessagesByOutboxStatusAndSagaStatuses(OutboxStatus outboxStatus,
                                                                          SagaStatus... sagaStatuses) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME ,outboxStatus, sagaStatuses);
    }
}
