package com.foodorder.service.restaurant.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.foodorder.service.restaurant.dataaccess", "com.foodorder.dataaccess" })
@EntityScan(basePackages = { "com.foodorder.service.restaurant.dataaccess", "com.foodorder.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.foodorder")
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
