package com.foodorder.service.order.dataaccess.order.entity;

import java.util.Objects;

public class OrderItemEntityId {

    private Long id;
    private OrderEntity order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntityId that = (OrderItemEntityId) o;
        return id.equals(that.id) && order.equals(that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}
