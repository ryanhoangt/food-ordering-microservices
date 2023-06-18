package com.foodorder.service.payment.domain.valueobject;

import com.foodorder.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID idValue) {
        super(idValue);
    }
}
