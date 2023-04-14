package com.foodorder.service.order.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long idValue) {
        super(idValue);
    }
}
