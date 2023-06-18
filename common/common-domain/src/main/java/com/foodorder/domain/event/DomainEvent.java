package com.foodorder.domain.event;

/**
 * @param <T> the type of the Entity object firing this event
 */
public interface DomainEvent<T> {
    void fire();
}
