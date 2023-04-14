package com.foodorder.service.order.domain.port.output.repository;

import com.foodorder.service.order.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);
}
