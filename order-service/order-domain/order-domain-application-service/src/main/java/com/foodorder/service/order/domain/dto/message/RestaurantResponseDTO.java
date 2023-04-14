package com.foodorder.service.order.domain.dto.message;

import com.foodorder.domain.valueobject.RestaurantValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantResponseDTO {
    private final String id;
    private final String sagaId;
    private final String orderId;
    private final String restaurantId;
    private Instant createdAt;
    private RestaurantValidationStatus validationStatus;
    private List<String> failureMessages;
}
