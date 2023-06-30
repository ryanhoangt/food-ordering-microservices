package com.foodorder.service.restaurant.messaging.mapper;


import com.foodorder.domain.valueobject.ProductId;
import com.foodorder.domain.valueobject.RestaurantOrderStatus;
import com.foodorder.kafka.order.avro.model.RestaurantRequestAvroModel;
import com.foodorder.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.foodorder.kafka.order.avro.model.RestaurantValidationStatus;
import com.foodorder.service.restaurant.domain.dto.RestaurantApprovalRequestDTO;
import com.foodorder.service.restaurant.domain.entity.Product;
import com.foodorder.service.restaurant.domain.event.OrderApprovedEvent;
import com.foodorder.service.restaurant.domain.event.OrderRejectedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {
    public RestaurantResponseAvroModel
    fromOrderApprovedEventToRestaurantResponseAvroModel(OrderApprovedEvent orderApprovedEvent) {
        return RestaurantResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getIdValue().toString())
                .setRestaurantId(orderApprovedEvent.getRestaurantId().getIdValue().toString())
                .setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
                .setValidationStatus(RestaurantValidationStatus.valueOf(orderApprovedEvent.
                        getOrderApproval().getApprovalStatus().name()))
                .setFailureMessages(orderApprovedEvent.getFailureMessages())
                .build();
    }

    public RestaurantResponseAvroModel
    fromOrderRejectedEventToRestaurantResponseAvroModel(OrderRejectedEvent orderRejectedEvent) {
        return RestaurantResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getIdValue().toString())
                .setRestaurantId(orderRejectedEvent.getRestaurantId().getIdValue().toString())
                .setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
                .setValidationStatus(RestaurantValidationStatus.valueOf(orderRejectedEvent.
                        getOrderApproval().getApprovalStatus().name()))
                .setFailureMessages(orderRejectedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequestDTO fromRestaurantRequestAvroModelToRestaurantApprovalRequestDTO (
            RestaurantRequestAvroModel restaurantRequestAvroModel) {
        return RestaurantApprovalRequestDTO.builder()
                .id(restaurantRequestAvroModel.getId())
                .sagaId(restaurantRequestAvroModel.getSagaId())
                .restaurantId(restaurantRequestAvroModel.getRestaurantId())
                .orderId(restaurantRequestAvroModel.getOrderId())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantRequestAvroModel
                        .getRestaurantOrderStatus().name()))
                .products(restaurantRequestAvroModel.getProducts()
                        .stream().map(avroModel ->
                                Product.Builder.builder()
                                        .productId(new ProductId(UUID.fromString(avroModel.getId())))
                                        .quantity(avroModel.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(restaurantRequestAvroModel.getPrice())
                .createdAt(restaurantRequestAvroModel.getCreatedAt())
                .build();
    }
}
