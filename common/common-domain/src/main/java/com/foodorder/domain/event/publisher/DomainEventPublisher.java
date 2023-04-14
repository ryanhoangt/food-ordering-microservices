package com.foodorder.domain.event.publisher;

import com.foodorder.domain.event.DomainEvent;

public interface DomainEventPublisher<DE extends DomainEvent<T>, T> {

    void publish(DE domainEvent);
}
