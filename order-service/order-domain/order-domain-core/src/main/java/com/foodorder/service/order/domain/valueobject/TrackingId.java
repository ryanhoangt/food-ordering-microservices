package com.foodorder.service.order.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID idValue) {
        super(idValue);
    }
}
