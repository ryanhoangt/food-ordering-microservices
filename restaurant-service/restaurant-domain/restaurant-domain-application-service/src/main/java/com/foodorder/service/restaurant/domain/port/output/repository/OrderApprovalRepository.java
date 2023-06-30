package com.foodorder.service.restaurant.domain.port.output.repository;

import com.foodorder.service.restaurant.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
