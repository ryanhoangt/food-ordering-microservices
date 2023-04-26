package com.foodorder.service.order.messaging.listener.kafka;

import com.foodorder.kafka.consumer.KafkaConsumer;
import com.foodorder.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.foodorder.kafka.order.avro.model.RestaurantValidationStatus;
import com.foodorder.service.order.domain.entity.Order;
import com.foodorder.service.order.domain.port.input.message.listener.restaurant.RestaurantResponseMsgListener;
import com.foodorder.service.order.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantResponseKafkaListener implements KafkaConsumer<RestaurantResponseAvroModel> {

    private final RestaurantResponseMsgListener restaurantResponseMsgListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantResponseKafkaListener(RestaurantResponseMsgListener restaurantResponseMsgListener,
                                           OrderMessagingDataMapper orderMessagingDataMapper) {
        this.restaurantResponseMsgListener = restaurantResponseMsgListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-consumer-group-id}",
                topics = "${order-service.restaurant-response-topic-name}")
    public void receive(@Payload List<RestaurantResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of restaurant responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(responseModel -> {
            if (responseModel.getValidationStatus() == RestaurantValidationStatus.APPROVED) {
                log.info("Processing approved order for id: {}", responseModel.getOrderId());
                restaurantResponseMsgListener.orderApprovedHandler(
                        orderMessagingDataMapper.fromRestaurantResponseAvroModelToRestaurantResponseDTO(responseModel)
                );
            } else if (responseModel.getValidationStatus() == RestaurantValidationStatus.REJECTED) {
                log.info("Processing rejected order for order id: {} with failure messages: {}",
                        responseModel.getOrderId(),
                        String.join(Order.FAILURE_MESSAGE_DELIMITER, responseModel.getFailureMessages()));
                restaurantResponseMsgListener.orderRejectedHandler(
                        orderMessagingDataMapper.fromRestaurantResponseAvroModelToRestaurantResponseDTO(responseModel)
                );
            }
        });
    }
}
