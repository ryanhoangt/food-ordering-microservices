package com.foodorder.service.order.domain.port.output.repository;

import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalOutboxRepository {
    OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage);
    Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                      OutboxStatus outboxStatus,
                                                                                      SagaStatus... sagaStatuses);
    Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                         UUID sagaId,
                                                                         SagaStatus... sagaStatuses);
    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatuses);
}
