package com.foodorder.domain.valueobject;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {

    public ProductId(UUID idValue) {
        super(idValue);
    }
}
