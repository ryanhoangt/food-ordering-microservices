package com.foodorder.service.restaurant.dataaccess.adapter;

import com.foodorder.dataaccess.restaurant.entity.RestaurantEntity;
import com.foodorder.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.foodorder.service.restaurant.dataaccess.mapper.RestaurantDataAccessMapper;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import com.foodorder.service.restaurant.domain.port.output.repository.RestaurantRepository;
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
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts =
                restaurantDataAccessMapper.fromRestaurantToProductIdsList(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getIdValue(),
                        restaurantProducts);
        return restaurantEntities.map(restaurantDataAccessMapper::fromRestaurantEntitiesListToRestaurant);
    }
}
