package com.foodorder.service.order.domain.mapper;

import com.foodorder.domain.valueobject.*;
import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.create.OrderAddressDTO;
import com.foodorder.service.order.domain.dto.create.OrderItemDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderResponseDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.OrderItem;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.foodorder.service.order.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.foodorder.service.order.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant fromRequestDTOToRestaurant(CreateOrderRequestDTO requestDTO) {
        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(requestDTO.getRestaurantId()))
                .products(requestDTO.getItems()
                        .stream()
                        .map(orderItemDto -> new Product(new ProductId(orderItemDto.getProductId())))
                        .collect(Collectors.toList()))
                .build();
    }

    public Order fromRequestDTOToOrder(CreateOrderRequestDTO requestDTO) {
        return Order.Builder.builder()
                .customerId(new CustomerId(requestDTO.getCustomerId()))
                .restaurantId(new RestaurantId(requestDTO.getRestaurantId()))
                .deliveryAddress(fromOrderAddrDTOToStreetAddr(requestDTO.getAddress()))
                .price(new Money(requestDTO.getPrice()))
                .items(fromOrderItemDTOListToOrderItemEntityList(requestDTO.getItems()))
                .build();
    }

    public CreateOrderResponseDTO fromOrderToCreateOrderResponseDTO(Order order, String message) {
        return CreateOrderResponseDTO.builder()
                .orderTrackingId(order.getTrackingId().getIdValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponseDTO fromOrderToTrackOrderResponseDTO(Order order) {
        return TrackOrderResponseDTO.builder()
                .orderTrackingId(order.getTrackingId().getIdValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> fromOrderItemDTOListToOrderItemEntityList(List<OrderItemDTO> itemDtoList) {
        return itemDtoList
                .stream()
                .map(itemDto -> OrderItem.Builder.builder()
                        .product(new Product(new ProductId(itemDto.getProductId())))
                        .price(new Money(itemDto.getPrice()))
                        .quantity(itemDto.getQuantity())
                        .subTotal(new Money(itemDto.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress fromOrderAddrDTOToStreetAddr(OrderAddressDTO addressDTO) {
        return new StreetAddress(
                UUID.randomUUID(),
                addressDTO.getStreet(),
                addressDTO.getPostalCode(),
                addressDTO.getCity()
        );
    }

    public OrderPaymentEventPayload fromOrderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCreatedEvent.getOrder().getCustomerId().getIdValue().toString())
                .orderId(orderCreatedEvent.getOrder().getId().getIdValue().toString())
                .price(orderCreatedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCreatedEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderPaymentEventPayload fromOrderCancelInitiatedEventToOrderPaymentEventPayload(OrderCancelInitiatedEvent orderCancelInitiatedEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCancelInitiatedEvent.getOrder().getCustomerId().getIdValue().toString())
                .orderId(orderCancelInitiatedEvent.getOrder().getId().getIdValue().toString())
                .price(orderCancelInitiatedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCancelInitiatedEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .build();
    }

    public OrderApprovalEventPayload fromOrderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
        return OrderApprovalEventPayload.builder()
                .orderId(orderPaidEvent.getOrder().getId().getIdValue().toString())
                .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getIdValue().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.getOrder().getItems().stream().map(
                        orderItem -> OrderApprovalEventProduct.builder().build()
                    ).collect(Collectors.toList())
                )
                .price(orderPaidEvent.getOrder().getPrice().getAmount())
                .createdAt(orderPaidEvent.getCreatedAt())
                .build();
    }
}