package com.foodorder.service.payment.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
    public CreditEntryId(UUID idValue) {
        super(idValue);
    }
}
