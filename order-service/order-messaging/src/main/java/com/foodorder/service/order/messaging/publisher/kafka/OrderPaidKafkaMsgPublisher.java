package com.foodorder.service.order.messaging.publisher.kafka;

import com.foodorder.kafka.order.avro.model.RestaurantRequestAvroModel;
import com.foodorder.kafka.producer.KafkaMsgHelper;
import com.foodorder.kafka.producer.service.KafkaProducer;
import com.foodorder.service.order.domain.config.OrderServiceConfigData;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.port.output.message.publisher.restaurant.OrderPaidRestaurantRequestMsgPublisher;
import com.foodorder.service.order.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPaidKafkaMsgPublisher implements OrderPaidRestaurantRequestMsgPublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantRequestAvroModel> kafkaProducer;
    private final KafkaMsgHelper kafkaMsgHelper;

    public OrderPaidKafkaMsgPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                      OrderServiceConfigData orderServiceConfigData,
                                      KafkaProducer<String, RestaurantRequestAvroModel> kafkaProducer,
                                      KafkaMsgHelper kafkaMsgHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.kafkaMsgHelper = kafkaMsgHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getIdValue().toString();

        try {
            RestaurantRequestAvroModel model = orderMessagingDataMapper.fromOrderPaidEventToRestaurantRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    model,
                    kafkaMsgHelper.getCallback(
                            orderServiceConfigData.getPaymentRequestTopicName(),
                            model,
                            orderId,
                            "RestaurantRequestAvroModel"
                    )
            );

            log.info("RestaurantRequestAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantRequestAvroModel message to Kafka " +
                    "with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
