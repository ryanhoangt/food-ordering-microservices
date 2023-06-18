package com.foodorder.service.payment.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.foodorder.service.payment.dataaccess")
@EntityScan(basePackages = "com.foodorder.service.payment.dataaccess")
@SpringBootApplication(scanBasePackages = "com.foodorder")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
