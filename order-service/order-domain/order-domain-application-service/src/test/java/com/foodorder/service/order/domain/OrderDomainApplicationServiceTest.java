package com.foodorder.service.order.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.domain.valueobject.*;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.saga.SagaStatus;
import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.create.OrderAddressDTO;
import com.foodorder.service.order.domain.dto.create.OrderItemDTO;
import com.foodorder.service.order.domain.entity.Customer;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.entity.Product;
import com.foodorder.service.order.domain.entity.Restaurant;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.mapper.OrderDataMapper;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.foodorder.service.order.domain.port.input.service.OrderDomainApplicationService;
import com.foodorder.service.order.domain.port.output.repository.CustomerRepository;
import com.foodorder.service.order.domain.port.output.repository.OrderRepository;
import com.foodorder.service.order.domain.port.output.repository.PaymentOutboxRepository;
import com.foodorder.service.order.domain.port.output.repository.RestaurantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.foodorder.saga.order.SagaConstants.ORDER_SAGA_NAME;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderDomainApplicationServiceTest {
    @Autowired
    private OrderDomainApplicationService orderDomainApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderRequestDTO requestDTO;
    private CreateOrderRequestDTO requestDTOWrongPrice;
    private CreateOrderRequestDTO requestDTOWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        requestDTO = CreateOrderRequestDTO.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDTO.builder()
                        .street("street_1")
                        .postalCode("70000")
                        .city("HCM_City")
                        .build())
                .price(PRICE)
                .items(List.of(
                        OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build()
                        , OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        requestDTOWrongPrice = CreateOrderRequestDTO.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDTO.builder()
                        .street("street_1")
                        .postalCode("70000")
                        .city("HCM_City")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(
                        OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build()
                        , OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        requestDTOWrongProductPrice = CreateOrderRequestDTO.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDTO.builder()
                        .street("street_1")
                        .postalCode("70000")
                        .city("HCM_City")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(
                        OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build()
                        , OrderItemDTO.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurant = Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .isActive(true)
                .build();

        Order order = orderDataMapper.fromRequestDTOToOrder(requestDTO);
        order.setId(new OrderId(ORDER_ID));

        // mock method responses
        Mockito.when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        Mockito.when(restaurantRepository.findRestaurantInfomation(orderDataMapper.fromRequestDTOToRestaurant(requestDTO)))
                .thenReturn(Optional.of(restaurant));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(paymentOutboxRepository.save(Mockito.any(OrderPaymentOutboxMessage.class)))
                .thenReturn(getOrderPaymentOutboxMessage());
    }

    @Test
    public void testCreateOrder() {
        CreateOrderResponseDTO responseDTO = orderDomainApplicationService.createOrder(requestDTO);
        Assertions.assertEquals(OrderStatus.PENDING, responseDTO.getOrderStatus());
        Assertions.assertEquals("Order created successfully", responseDTO.getMessage());
        Assertions.assertNotNull(responseDTO.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = Assertions.assertThrows(
                OrderDomainException.class,
                () -> orderDomainApplicationService.createOrder(requestDTOWrongPrice)
        );
        Assertions.assertEquals(
                "Total price: 250.00 is not equal to order items' total: 200.00.",
                orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException = Assertions.assertThrows(
                OrderDomainException.class,
                () -> orderDomainApplicationService.createOrder(requestDTOWrongProductPrice)
        );
        Assertions.assertEquals(
                "Order item price: 60.00 is not valid for product " + PRODUCT_ID + ".",
                orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithInactiveRestaurant() {
        Restaurant inactiveRestaurant = Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .isActive(false)
                .build();

        Mockito.when(restaurantRepository.findRestaurantInfomation(orderDataMapper.fromRequestDTOToRestaurant(requestDTO)))
                .thenReturn(Optional.of(inactiveRestaurant));

        OrderDomainException orderDomainException = Assertions.assertThrows(
                OrderDomainException.class,
                () -> orderDomainApplicationService.createOrder(requestDTO)
        );
        Assertions.assertEquals(
                "Restaurant with id " + RESTAURANT_ID + " is currently not active.",
                orderDomainException.getMessage());
    }

    private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
        OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(ZonedDateTime.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();

        return OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(SAGA_ID)
                .createdAt(ZonedDateTime.now())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderPaymentEventPayload))
                .orderStatus(OrderStatus.PENDING)
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            throw new OrderDomainException("Cannot create OrderPaymentEventPayload object!");
        }
    }
}
