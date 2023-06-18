package com.foodorder.service.order.messaging.publisher.kafka;

import com.foodorder.kafka.order.avro.model.PaymentRequestAvroModel;
import com.foodorder.kafka.producer.KafkaMsgHelper;
import com.foodorder.kafka.producer.service.KafkaProducer;
import com.foodorder.service.order.domain.config.OrderServiceConfigData;
import com.foodorder.service.order.domain.event.OrderCancelInitiatedEvent;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMsgPublisher;
import com.foodorder.service.order.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCancelledKafkaMsgPublisher implements OrderCancelledPaymentRequestMsgPublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaMsgHelper kafkaMsgHelper;

    public OrderCancelledKafkaMsgPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                           OrderServiceConfigData orderServiceConfigData,
                                           KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer, KafkaMsgHelper kafkaMsgHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.kafkaMsgHelper = kafkaMsgHelper;
    }

    @Override
    public void publish(OrderCancelInitiatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getIdValue().toString();
        log.info("Received OrderCancelInitiatedEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel model = orderMessagingDataMapper.fromOrderCancelInitiatedEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    model,
                    kafkaMsgHelper.getCallback(
                            orderServiceConfigData.getPaymentRequestTopicName(),
                            model,
                            orderId,
                            "PaymentRequestAvroModel"
                    )
            );

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", model.getOrderId());
        } catch (Exception ex) {
            log.error("Error while sending PaymentRequestAvroModel message " +
                    "to Kafka with order id: {}, error: {}", orderId, ex.getMessage());
        }
    }

}
