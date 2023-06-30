package com.foodorder.service.order.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.foodorder.service.order.dataaccess", "com.foodorder.dataaccess" })
@EntityScan(basePackages = { "com.foodorder.service.order.dataaccess", "com.foodorder.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.foodorder")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
