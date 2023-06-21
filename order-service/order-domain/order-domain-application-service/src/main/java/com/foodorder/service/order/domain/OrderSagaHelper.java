package com.foodorder.service.order.domain;

import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.exception.OrderNotFoundException;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

     Order findOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderOpt.isEmpty()) {
            String errorMsg = "Order with id=" + orderId + " could not be found.";
            log.error(errorMsg);
            throw new OrderNotFoundException(errorMsg);
        }
        return orderOpt.get();
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
