package com.foodorder.service.order.domain.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.ProductId;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.create.OrderAddressDTO;
import com.foodorder.service.order.domain.dto.create.OrderItemDTO;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.OrderItem;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.entity.Restaurant;
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

    public CreateOrderResponseDTO fromOrderToResponseDTO(Order order) {
        return CreateOrderResponseDTO.builder()
                .orderTrackingId(order.getTrackingId().getIdValue())
                .orderStatus(order.getOrderStatus())
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
}
