package com.foodorder.service.order.domain.port;

import com.foodorder.service.order.domain.OrderDomainService;
import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.entity.Customer;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.event.OrderCreatedEvent;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.port.output.repository.CustomerRepository;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import com.foodorder.service.order.domain.port.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CreateOrderRequestHandler {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;
    private final ApplicationDomainEventPublisher applicationDomainEventPublisher;

    public CreateOrderRequestHandler(OrderDomainService orderDomainService,
                                     OrderRepository orderRepository,
                                     CustomerRepository customerRepository,
                                     RestaurantRepository restaurantRepository,
                                     OrderDataMapper orderDataMapper,
                                     ApplicationDomainEventPublisher applicationDomainEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
        this.applicationDomainEventPublisher = applicationDomainEventPublisher;
    }

    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        checkCustomer(requestDTO.getCustomerId());
        Restaurant validRestaurant = checkRestaurantAndGet(requestDTO);
        Order reqOrder = orderDataMapper.fromRequestDTOToOrder(requestDTO);

        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(reqOrder, validRestaurant);
        Order savedOrder = saveOrder(reqOrder);
        log.info("Order is initiated with id: {}", savedOrder.getId().getIdValue());

        applicationDomainEventPublisher.publish(orderCreatedEvent);
        return orderDataMapper.fromOrderToResponseDTO(savedOrder);
    }

    private Order saveOrder(Order reqOrder) {
        Order savedOrder = orderRepository.save(reqOrder);
        if (savedOrder == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", savedOrder.getId().getIdValue());
        return savedOrder;
    }

    private Restaurant checkRestaurantAndGet(CreateOrderRequestDTO requestDTO) {
        Restaurant reqRestaurant = orderDataMapper.fromRequestDTOToRestaurant(requestDTO);
        Optional<Restaurant> restaurantOpt = restaurantRepository.findRestaurantInfomation(reqRestaurant);
        if (restaurantOpt.isEmpty()) {
            log.warn("Could not find restaurant with restaurant id: {}", requestDTO.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: " + requestDTO.getRestaurantId());
        }
        return restaurantOpt.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customerOpt = customerRepository.findCustomer(customerId);
        if (customerOpt.isEmpty()) {
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customerId);
        }
    }
}
