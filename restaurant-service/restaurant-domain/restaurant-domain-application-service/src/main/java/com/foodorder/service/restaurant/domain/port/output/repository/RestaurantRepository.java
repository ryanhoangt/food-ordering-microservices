package com.foodorder.service.restaurant.domain.port.output.repository;

import com.foodorder.service.restaurant.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
