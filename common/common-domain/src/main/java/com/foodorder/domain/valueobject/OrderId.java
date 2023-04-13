package com.foodorder.domain.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<UUID> {

    public OrderId(UUID idValue) {
        super(idValue);
    }
}
