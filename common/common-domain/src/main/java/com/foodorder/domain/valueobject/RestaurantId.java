package com.foodorder.domain.valueobject;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID> {

    public RestaurantId(UUID idValue) {
        super(idValue);
    }
}
