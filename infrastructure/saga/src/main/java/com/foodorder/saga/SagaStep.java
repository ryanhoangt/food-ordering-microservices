package com.foodorder.saga;

import com.foodorder.domain.event.DomainEvent;

public interface SagaStep<T>  {

    void process(T data);
    void rollback(T data);
}
