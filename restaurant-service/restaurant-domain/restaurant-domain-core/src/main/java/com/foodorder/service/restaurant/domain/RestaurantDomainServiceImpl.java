package com.foodorder.service.restaurant.domain;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.domain.valueobject.RestaurantValidationStatus;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.entity.Restaurant;
import com.foodorder.service.restaurant.domain.event.OrderApprovalEvent;
import com.foodorder.service.restaurant.domain.event.OrderApprovedEvent;
import com.foodorder.service.restaurant.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.foodorder.domain.DomainConstants.UTC_ZONE_ID;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant,
                                            List<String> failureMessages,
                                            DomainEventPublisher<OrderApprovedEvent, OrderApproval> orderApprovedEventPublisher,
                                            DomainEventPublisher<OrderRejectedEvent, OrderApproval> orderRejectedEventPublisher) {
        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getIdValue());

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getIdValue());
            restaurant.constructOrderApproval(RestaurantValidationStatus.APPROVED);
            return new OrderApprovedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)),
                    orderApprovedEventPublisher
            );
        }
        log.info("Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getIdValue());
        restaurant.constructOrderApproval(RestaurantValidationStatus.REJECTED);
        return new OrderRejectedEvent(
                restaurant.getOrderApproval(),
                restaurant.getId(),
                failureMessages,
                ZonedDateTime.now(ZoneId.of(UTC_ZONE_ID)),
                orderRejectedEventPublisher
        );
    }
}
