package com.foodorder.service.restaurant.dataaccess.entity;

import com.foodorder.domain.valueobject.RestaurantValidationStatus;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "restaurant", name = "order_approval")
public class OrderApprovalEntity {

    @Id
    private UUID id;
    private UUID restaurantId;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private RestaurantValidationStatus status;
}
