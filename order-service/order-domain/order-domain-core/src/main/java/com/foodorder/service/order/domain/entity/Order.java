package com.foodorder.service.order.domain.entity;

import com.foodorder.domain.entity.AggregateRoot;
import com.foodorder.domain.valueobject.*;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.valueobject.OrderItemId;
import com.foodorder.service.order.domain.valueobject.StreetAddress;
import com.foodorder.service.order.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    /**
     * Initialize the order after validate initial status.
     */
    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    /**
     * Validate the order prior to the call of order initialization.
     */
    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    /**
     * This method is called after a successful response from the payment service.
     */
    public void pay() {
        if (orderStatus != OrderStatus.PENDING)
            throw new OrderDomainException("Order is not in the correct state for 'pay' operation.");

        orderStatus = OrderStatus.PAID;
    }

    /**
     * This method is called after a successful response from the restaurant service.
     */
    public void approve() {
        if (orderStatus != OrderStatus.PAID)
            throw new OrderDomainException("Order is not in the correct state for 'approve' operation.");

        orderStatus = OrderStatus.APPROVED;
    }

    /**
     * This method is called after the restaurant service rejects the order.
     * In that case, perform a compensating transaction.
     */
    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID)
            throw new OrderDomainException("Order is not in the correct state for 'initCancel' operation.");

        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    /**
     * This method could be invoked in two stages:
     * 1) When the order is in PENDING state, and the payment service failed to serve the order.
     * 2) When the order is in CANCELLING state, and the rollback operation has been performed.
     */
    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING))
            throw new OrderDomainException("Order is not in the correct state for 'cancel' operation.");

        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null)
            this.failureMessages.addAll(failureMessages);
        if (this.failureMessages == null)
            this.failureMessages = failureMessages;
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null)
            throw new OrderDomainException("Order is not in the correct state for initialization.");
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero())
            throw new OrderDomainException("Total price must be greater than 0.");
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal))
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is not equal to order items' total: " + orderItemsTotal.getAmount() + ".");
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid())
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount()
            + " is not valid for product " + orderItem.getProduct().getId().getIdValue() + ".");
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem item: items) {
            item.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
