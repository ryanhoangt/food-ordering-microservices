package com.foodorder.service.restaurant.dataaccess.adapter;

import com.foodorder.service.restaurant.dataaccess.mapper.RestaurantDataAccessMapper;
import com.foodorder.service.restaurant.dataaccess.repository.OrderApprovalJpaRepository;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.port.output.repository.OrderApprovalRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository orderApprovalJpaRepository,
                                       RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.orderApprovalJpaRepository = orderApprovalJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataAccessMapper.fromOrderApprovalEntityToOrderApproval(
                orderApprovalJpaRepository.save(restaurantDataAccessMapper.fromOrderApprovalToOrderApprovalEntity(orderApproval))
        );
    }
}
