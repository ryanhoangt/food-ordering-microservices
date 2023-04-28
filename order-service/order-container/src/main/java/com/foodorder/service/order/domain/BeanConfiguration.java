package com.foodorder.service.order.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean // for injecting into OrderDomainApplicationService
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
