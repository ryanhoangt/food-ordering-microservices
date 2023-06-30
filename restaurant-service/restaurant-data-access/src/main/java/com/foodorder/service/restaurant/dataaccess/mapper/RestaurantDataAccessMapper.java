package com.foodorder.service.restaurant.dataaccess.mapper;

import com.foodorder.dataaccess.restaurant.entity.RestaurantEntity;
import com.foodorder.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.domain.valueobject.ProductId;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.restaurant.dataaccess.entity.OrderApprovalEntity;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.entity.OrderDetail;
import com.foodorder.service.restaurant.domain.entity.Product;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import com.foodorder.service.restaurant.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> fromRestaurantToProductIdsList(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts().stream()
                .map(product -> product.getId().getIdValue())
                .collect(Collectors.toList());
    }

    public Restaurant fromRestaurantEntitiesListToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("No restaurants found!"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                        Product.Builder.builder()
                                .productId(new ProductId(entity.getProductId()))
                                .name(entity.getProductName())
                                .price(new Money(entity.getProductPrice()))
                                .available(entity.getProductAvailable())
                                .build())
                .collect(Collectors.toList());

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .orderDetail(OrderDetail.Builder.builder()
                        .products(restaurantProducts)
                        .build())
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

    public OrderApprovalEntity fromOrderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getIdValue())
                .restaurantId(orderApproval.getRestaurantId().getIdValue())
                .orderId(orderApproval.getOrderId().getIdValue())
                .status(orderApproval.getApprovalStatus())
                .build();
    }

    public OrderApproval fromOrderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
        return OrderApproval.Builder.builder()
                .orderApprovalId(new OrderApprovalId(orderApprovalEntity.getId()))
                .restaurantId(new RestaurantId(orderApprovalEntity.getRestaurantId()))
                .orderId(new OrderId(orderApprovalEntity.getOrderId()))
                .approvalStatus(orderApprovalEntity.getStatus())
                .build();
    }

}
