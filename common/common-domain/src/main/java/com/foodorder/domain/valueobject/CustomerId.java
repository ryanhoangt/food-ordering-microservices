package com.foodorder.domain.valueobject;

import java.util.UUID;

public class CustomerId extends BaseId<UUID> {

    public CustomerId(UUID idValue) {
        super(idValue);
    }
}
