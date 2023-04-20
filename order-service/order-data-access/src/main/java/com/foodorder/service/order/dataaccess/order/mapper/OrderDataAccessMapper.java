package com.foodorder.service.order.dataaccess.order.mapper;

import com.foodorder.domain.valueobject.*;
import com.foodorder.service.order.dataaccess.order.entity.OrderAddressEntity;
import com.foodorder.service.order.dataaccess.order.entity.OrderEntity;
import com.foodorder.service.order.dataaccess.order.entity.OrderItemEntity;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.OrderItem;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.valueobject.OrderItemId;
import com.foodorder.service.order.domain.valueobject.StreetAddress;
import com.foodorder.service.order.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    /**
     * A method to map an object of Order class from domain module to an object
     * of OrderEntity class.
     */
    public OrderEntity fromOrderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getIdValue())
                .customerId(order.getCustomerId().getIdValue())
                .restaurantId(order.getRestaurantId().getIdValue())
                .trackingId(order.getTrackingId().getIdValue())
                .address(fromDeliveryAddressToOrderAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(fromOrderItemsToOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(Order.FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .build();

        // update fields in relationship-holder objects
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));

        return orderEntity;
    }

    /**
     * A method to map an object of OrderEntity class to an object of
     * Order class from domain module.
     */
    public Order fromOrderEntityToOrder(OrderEntity orderEntity) {
        return Order.Builder.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .deliveryAddress(fromAddressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(fromOrderItemEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>()
                        : new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(Order.FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> fromOrderItemEntitiesToOrderItems(List<OrderItemEntity> entityItems) {
        return entityItems.stream()
                .map(item -> OrderItem.Builder.builder()
                        .orderItemId(new OrderItemId(item.getId()))
                        .product(new Product(new ProductId(item.getProductId())))
                        .price(new Money(item.getPrice()))
                        .quantity(item.getQuantity())
                        .subTotal(new Money(item.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress fromAddressEntityToDeliveryAddress(OrderAddressEntity addressEntity) {
        return new StreetAddress(addressEntity.getId(),
                addressEntity.getStreet(), addressEntity.getPostalCode(), addressEntity.getCity());
    }

    private List<OrderItemEntity> fromOrderItemsToOrderItemEntities(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getIdValue())
                        .productId(orderItem.getOrderId().getIdValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderAddressEntity fromDeliveryAddressToOrderAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .city(deliveryAddress.getCity())
                .build();
    }

}
