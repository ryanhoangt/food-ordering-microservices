package com.foodorder.service.order.dataaccess.restaurant.mapper;

import com.foodorder.dataaccess.restaurant.entity.RestaurantEntity;
import com.foodorder.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.ProductId;
import com.foodorder.domain.valueobject.RestaurantId;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> fromRestaurantToProductIds(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getIdValue())
                .collect(Collectors.toList());
    }

    public Restaurant fromRestaurantEntitiesToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found!"));

        List<Product> products = restaurantEntities.stream()
                .map(entity -> new Product(
                        new ProductId(entity.getProductId()),
                        entity.getProductName(),
                        new Money(entity.getProductPrice())
                ))
                .collect(Collectors.toList());

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(products)
                .isActive(restaurantEntity.getRestaurantActive())
                .build();
    }
}
