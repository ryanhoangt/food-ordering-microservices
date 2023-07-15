package com.foodorder.service.order.messaging.mapper;

import com.foodorder.domain.valueobject.PaymentStatus;
import com.foodorder.domain.valueobject.RestaurantValidationStatus;
import com.foodorder.kafka.order.avro.model.*;
import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;
import com.foodorder.service.order.domain.dto.message.RestaurantResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel fromOrderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getIdValue().toString())
                .setOrderId(order.getId().getIdValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel fromOrderCancelInitiatedEventToPaymentRequestAvroModel(OrderCancelInitiatedEvent orderCancelInitiatedEvent) {
        Order order = orderCancelInitiatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getIdValue().toString())
                .setOrderId(order.getId().getIdValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelInitiatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED) // for rolling back tx
                .build();
    }

    public RestaurantRequestAvroModel fromOrderPaidEventToRestaurantRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return RestaurantRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(order.getId().getIdValue().toString())
                .setRestaurantId(order.getRestaurantId().getIdValue().toString())
                .setOrderId(order.getId().getIdValue().toString())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .setProducts(order.getItems().stream()
                        .map(orderItem -> Product.newBuilder()
                                .setId(orderItem.getProduct().getId().getIdValue().toString())
                                .setQuantity(orderItem.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public PaymentResponseDTO fromPaymentResponseAvroModelToPaymentResponseDTO(PaymentResponseAvroModel avroModel) {
        return PaymentResponseDTO.builder()
                .id(avroModel.getId())
                .sagaId(avroModel.getSagaId())
                .paymentId(avroModel.getPaymentId())
                .customerId(avroModel.getCustomerId())
                .orderId(avroModel.getOrderId())
                .price(avroModel.getPrice())
                .createdAt(avroModel.getCreatedAt())
                .paymentStatus(PaymentStatus.valueOf(avroModel.getPaymentStatus().name()))
                .failureMessages(avroModel.getFailureMessages())
                .build();
    }

    public RestaurantResponseDTO fromRestaurantResponseAvroModelToRestaurantResponseDTO(RestaurantResponseAvroModel avroModel) {
        return RestaurantResponseDTO.builder()
                .id(avroModel.getId())
                .sagaId(avroModel.getSagaId())
                .restaurantId(avroModel.getRestaurantId())
                .orderId(avroModel.getOrderId())
                .createdAt(avroModel.getCreatedAt())
                .validationStatus(RestaurantValidationStatus.valueOf(avroModel.getValidationStatus().name()))
                .failureMessages(avroModel.getFailureMessages())
                .build();
    }

    public PaymentRequestAvroModel fromOrderPaymentEventToPaymentRequestAvroModel(String sagaId, OrderPaymentEventPayload orderPaymentEventPayload) {
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setCustomerId(orderPaymentEventPayload.getCustomerId())
                .setOrderId(orderPaymentEventPayload.getOrderId())
                .setPrice(orderPaymentEventPayload.getPrice())
                .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }
}
