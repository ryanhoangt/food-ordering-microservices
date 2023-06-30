package com.foodorder.service.restaurant.messaging.publisher.kafka;

import com.foodorder.kafka.order.avro.model.RestaurantResponseAvroModel;
import com.foodorder.kafka.producer.KafkaMsgHelper;
import com.foodorder.kafka.producer.service.KafkaProducer;
import com.foodorder.service.restaurant.domain.config.RestaurantServiceConfigData;
import com.foodorder.service.restaurant.domain.event.OrderRejectedEvent;
import com.foodorder.service.restaurant.domain.port.output.message.publisher.OrderRejectedMsgPublisher;
import com.foodorder.service.restaurant.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMsgPublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMsgHelper kafkaMessageHelper;

    public OrderRejectedKafkaMessagePublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
                                              KafkaProducer<String, RestaurantResponseAvroModel> kafkaProducer,
                                              RestaurantServiceConfigData restaurantServiceConfigData,
                                              KafkaMsgHelper kafkaMessageHelper) {
        this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.restaurantServiceConfigData = restaurantServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderRejectedEvent orderRejectedEvent) {
        String orderId = orderRejectedEvent.getOrderApproval().getOrderId().getIdValue().toString();

        log.info("Received OrderRejectedEvent for order id: {}", orderId);

        try {
            RestaurantResponseAvroModel restaurantApprovalResponseAvroModel =
                    restaurantMessagingDataMapper
                            .fromOrderRejectedEventToRestaurantResponseAvroModel(orderRejectedEvent);

            kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    orderId,
                    restaurantApprovalResponseAvroModel,
                    kafkaMessageHelper.getCallback(restaurantServiceConfigData
                                    .getRestaurantApprovalResponseTopicName(),
                            restaurantApprovalResponseAvroModel,
                            orderId,
                            "RestaurantResponseAvroModel"));

            log.info("RestaurantResponseAvroModel sent to kafka at: {}", System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending RestaurantResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

}
