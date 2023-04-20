package com.foodorder.service.order.dataaccess.customer.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.service.order.dataaccess.customer.entity.CustomerEntity;
import com.foodorder.service.order.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer fromCustomerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
