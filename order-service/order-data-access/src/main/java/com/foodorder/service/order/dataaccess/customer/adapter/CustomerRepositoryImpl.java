package com.foodorder.service.order.dataaccess.customer.adapter;

import com.foodorder.service.order.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.foodorder.service.order.dataaccess.customer.repository.CustomerJpaRepository;
import com.foodorder.service.order.domain.entity.Customer;
import com.foodorder.service.order.domain.port.output.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository,
                                  CustomerDataAccessMapper customerDataAccessMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataAccessMapper = customerDataAccessMapper;
    }

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerJpaRepository.findById(customerId).map(customerDataAccessMapper::fromCustomerEntityToCustomer);
    }

}
