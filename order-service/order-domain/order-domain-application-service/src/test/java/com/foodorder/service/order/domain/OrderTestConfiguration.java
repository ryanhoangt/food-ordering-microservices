package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.port.output.message.publisher.payment.PaymentRequestOutboxMsgPublisher;
import com.foodorder.service.order.domain.port.output.message.publisher.restaurant.RestaurantRequestOutboxMsgPublisher;
import com.foodorder.service.order.domain.port.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.foodorder")
public class OrderTestConfiguration {

    /**
     * Mock all output ports that will be implemented by adapters in external modules
     */

    @Bean
    public PaymentRequestOutboxMsgPublisher paymentRequestOutboxMsgPublisher() {
        return Mockito.mock(PaymentRequestOutboxMsgPublisher.class);
    }

    @Bean
    public RestaurantRequestOutboxMsgPublisher restaurantRequestOutboxMsgPublisher() {
        return Mockito.mock(RestaurantRequestOutboxMsgPublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return Mockito.mock(PaymentOutboxRepository.class);
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository() {
        return Mockito.mock(ApprovalOutboxRepository.class);
    }

    /**
     * Create a Spring bean for OrderDomainService in order-domain-core, which is not
     * annotated by a Spring annotation.
     */
    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
