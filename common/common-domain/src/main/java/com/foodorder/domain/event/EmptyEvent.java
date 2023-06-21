package com.foodorder.domain.event;

// Marker class representing an empty event.
public final class EmptyEvent implements DomainEvent<Void> {

    public static final EmptyEvent INSTANCE = new EmptyEvent();

    private EmptyEvent() {
    }

    @Override
    public void fire() {
    }
}
