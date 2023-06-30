package com.foodorder.service.restaurant.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {

    public OrderApprovalId(UUID idValue) {
        super(idValue);
    }
}
