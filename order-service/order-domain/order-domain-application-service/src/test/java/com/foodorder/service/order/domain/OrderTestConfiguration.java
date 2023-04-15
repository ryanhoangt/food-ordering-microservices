package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMsgPublisher;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMsgPublisher;
import com.foodorder.service.order.domain.port.output.message.publisher.restaurant.OrderPaidRestaurantRequestMsgPublisher;
import com.foodorder.service.order.domain.port.output.repository.CustomerRepository;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import com.foodorder.service.order.domain.port.output.repository.RestaurantRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.foodorder")
public class OrderTestConfiguration {

    /**
     * Mock all output ports that will be implemented by adapters in external modules
     */

    @Bean
    public OrderCreatedPaymentRequestMsgPublisher orderCreatedPaymentRequestMsgPublisher() {
        return Mockito.mock(OrderCreatedPaymentRequestMsgPublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMsgPublisher orderCancelledPaymentRequestMsgPublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMsgPublisher.class);
    }

    @Bean
    public OrderPaidRestaurantRequestMsgPublisher orderPaidRestaurantRequestMsgPublisher() {
        return Mockito.mock(OrderPaidRestaurantRequestMsgPublisher.class);
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

    /**
     * Create a Spring bean for OrderDomainService in order-domain-core, which is not
     * annotated by a Spring annotation.
     */
    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
