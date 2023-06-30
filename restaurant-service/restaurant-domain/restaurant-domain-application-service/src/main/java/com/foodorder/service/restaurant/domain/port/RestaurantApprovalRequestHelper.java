package com.foodorder.service.restaurant.domain.port;

import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.service.restaurant.domain.RestaurantDomainService;
import com.foodorder.service.restaurant.domain.dto.RestaurantApprovalRequestDTO;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import com.foodorder.service.restaurant.domain.event.OrderApprovalEvent;
import com.foodorder.service.restaurant.domain.exception.RestaurantNotFoundException;
import com.foodorder.service.restaurant.domain.mapper.RestaurantDataMapper;
import com.foodorder.service.restaurant.domain.port.output.message.publisher.OrderApprovedMsgPublisher;
import com.foodorder.service.restaurant.domain.port.output.message.publisher.OrderRejectedMsgPublisher;
import com.foodorder.service.restaurant.domain.port.output.repository.OrderApprovalRepository;
import com.foodorder.service.restaurant.domain.port.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMsgPublisher orderApprovedMsgPublisher;
    private final OrderRejectedMsgPublisher orderRejectedMsgPublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           OrderApprovedMsgPublisher orderApprovedMsgPublisher,
                                           OrderRejectedMsgPublisher orderRejectedMsgPublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderApprovedMsgPublisher = orderApprovedMsgPublisher;
        this.orderRejectedMsgPublisher = orderRejectedMsgPublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequestDTO requestDTO) {
        log.info("Processing restaurant approval for order id: {}", requestDTO.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(requestDTO);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant,
                failureMessages, orderApprovedMsgPublisher, orderRejectedMsgPublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequestDTO requestDTO) {
        Restaurant restaurant = restaurantDataMapper.fromRestaurantApprovalRequestDTOToRestaurant(requestDTO);
        Optional<Restaurant> restaurantOpt = restaurantRepository.findRestaurantInformation(restaurant);
        if (restaurantOpt.isEmpty()) {
            String errorMsg = "Restaurant with id: " + restaurant.getId().getIdValue() + " not found.";
            log.error(errorMsg);
            throw new RestaurantNotFoundException(errorMsg);
        }

        Restaurant restaurantEntity = restaurantOpt.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
            restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                if (p.getId().equals(product.getId())) {
                    product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                }
            });
        });
        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(requestDTO.getOrderId())));
        return restaurant;
    }
}
