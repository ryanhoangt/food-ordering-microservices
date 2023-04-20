package com.foodorder.service.order.domain.entity;

import com.foodorder.domain.entity.AggregateRoot;
import com.foodorder.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
