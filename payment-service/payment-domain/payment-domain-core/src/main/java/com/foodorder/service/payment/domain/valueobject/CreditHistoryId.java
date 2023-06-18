package com.foodorder.service.payment.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditHistoryId extends BaseId<UUID> {
    public CreditHistoryId(UUID idValue) {
        super(idValue);
    }
}
