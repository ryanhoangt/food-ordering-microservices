package com.foodorder.service.restaurant.domain.mapper;

import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.domain.valueobject.OrderStatus;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.restaurant.domain.dto.RestaurantApprovalRequestDTO;
import com.foodorder.service.restaurant.domain.entity.OrderDetail;
import com.foodorder.service.restaurant.domain.entity.Product;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant fromRestaurantApprovalRequestDTOToRestaurant(RestaurantApprovalRequestDTO requestDTO) {
        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(UUID.fromString(requestDTO.getRestaurantId())))
                .orderDetail(OrderDetail.Builder.builder()
                        .orderId(new OrderId(UUID.fromString(requestDTO.getOrderId())))
                        .products(requestDTO.getProducts().stream().map(
                                product -> Product.Builder.builder()
                                        .productId(product.getId())
                                        .quantity(product.getQuantity())
                                        .build()
                        ).collect(Collectors.toList()))
                        .totalAmount(new Money(requestDTO.getPrice()))
                        .orderStatus(OrderStatus.valueOf(requestDTO.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }
}
