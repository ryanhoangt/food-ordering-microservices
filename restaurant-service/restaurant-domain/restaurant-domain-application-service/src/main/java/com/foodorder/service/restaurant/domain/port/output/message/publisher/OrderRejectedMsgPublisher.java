package com.foodorder.service.restaurant.domain.port.output.message.publisher;

import com.foodorder.domain.event.publisher.DomainEventPublisher;
import com.foodorder.service.restaurant.domain.entity.OrderApproval;
import com.foodorder.service.restaurant.domain.event.OrderRejectedEvent;

public interface OrderRejectedMsgPublisher extends DomainEventPublisher<OrderRejectedEvent, OrderApproval> {

}
