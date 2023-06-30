package com.foodorder.service.restaurant.domain.port.output.message.publisher;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.event.OrderApprovedEvent;

public interface OrderApprovedMsgPublisher extends DomainEventPublisher<OrderApprovedEvent, OrderApproval> {

}
