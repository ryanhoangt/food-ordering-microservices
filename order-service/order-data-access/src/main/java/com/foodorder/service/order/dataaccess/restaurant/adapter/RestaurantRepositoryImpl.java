package com.foodorder.service.order.dataaccess.restaurant.adapter;

import com.foodorder.service.order.dataaccess.restaurant.entity.RestaurantEntity;
import com.foodorder.service.order.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.foodorder.service.order.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.port.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository,
                                    RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInfomation(Restaurant candidateInfo) {
        List<UUID> productIds = restaurantDataAccessMapper.fromRestaurantToProductIds(candidateInfo);

        Optional<List<RestaurantEntity>> restaurantEntitiesOpt = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(candidateInfo.getId().getIdValue(), productIds);

        return restaurantEntitiesOpt.map(restaurantDataAccessMapper::fromRestaurantEntitiesToRestaurant);
    }
}
