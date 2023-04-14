package com.foodorder.service.order.domain.port.output.repository;

import com.foodorder.service.order.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurantInfomation(Restaurant candidateInfo);
}
