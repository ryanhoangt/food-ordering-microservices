package com.foodorder.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();
}
