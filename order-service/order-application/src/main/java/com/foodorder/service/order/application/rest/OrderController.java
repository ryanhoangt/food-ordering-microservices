package com.foodorder.service.order.application.rest;

import com.foodorder.service.order.domain.dto.create.CreateOrderRequestDTO;
import com.foodorder.service.order.domain.dto.create.CreateOrderResponseDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderRequestDTO;
import com.foodorder.service.order.domain.dto.track.TrackOrderResponseDTO;
import com.foodorder.service.order.domain.port.input.service.OrderDomainApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

    private final OrderDomainApplicationService orderDomainApplicationService;

    public OrderController(OrderDomainApplicationService orderDomainApplicationService) {
        this.orderDomainApplicationService = orderDomainApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponseDTO> createOrder(@RequestBody CreateOrderRequestDTO requestDTO) {
        log.info("Creating order for customer: {} at restaurant: {}",
                requestDTO.getCustomerId(),
                requestDTO.getRestaurantId());
        CreateOrderResponseDTO responseDTO = orderDomainApplicationService.createOrder(requestDTO);
        log.info("Order created with tracking id: {}", responseDTO.getOrderTrackingId());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponseDTO> trackOrderByTrackingId(@PathVariable UUID trackingId) {
        TrackOrderResponseDTO responseDTO = orderDomainApplicationService.trackOrder(
                TrackOrderRequestDTO.builder().orderTrackingId(trackingId).build());
        log.info("Returning order status with tracking id: {}", responseDTO.getOrderTrackingId());
        return ResponseEntity.ok(responseDTO);
    }
}
