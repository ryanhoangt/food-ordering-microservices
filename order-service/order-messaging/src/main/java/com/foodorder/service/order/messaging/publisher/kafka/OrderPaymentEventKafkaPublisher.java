package com.foodorder.service.order.messaging.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.kafka.order.avro.model.PaymentRequestAvroModel;
import com.foodorder.kafka.producer.KafkaMsgHelper;
import com.foodorder.kafka.producer.service.KafkaProducer;
import com.foodorder.outbox.OutboxStatus;
import com.foodorder.service.order.domain.config.OrderServiceConfigData;
import com.foodorder.service.order.domain.exception.OrderDomainException;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.foodorder.service.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.foodorder.service.order.domain.port.output.message.publisher.payment.PaymentRequestOutboxMsgPublisher;
import com.foodorder.service.order.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderPaymentEventKafkaPublisher implements PaymentRequestOutboxMsgPublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMsgHelper kafkaMsgHelper;
    private final ObjectMapper objectMapper;

    public OrderPaymentEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                           KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                           OrderServiceConfigData orderServiceConfigData,
                                           KafkaMsgHelper kafkaMsgHelper,
                                           ObjectMapper objectMapper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaMsgHelper = kafkaMsgHelper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage, BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
        OrderPaymentEventPayload eventPayload = getOrderPaymentEventPayload(orderPaymentOutboxMessage.getPayload());

        String sagaId = orderPaymentOutboxMessage.getSagaId().toString();
        log.info("Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
                eventPayload.getOrderId(), sagaId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.fromOrderPaymentEventToPaymentRequestAvroModel(sagaId, eventPayload);
            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(), sagaId, paymentRequestAvroModel,
                    kafkaMsgHelper.getCallback(
                            orderServiceConfigData.getPaymentRequestTopicName(),
                            paymentRequestAvroModel,
                            orderPaymentOutboxMessage,
                            outboxCallback,
                            eventPayload.getOrderId(),
                            "PaymentRequestAvroModel"));

            log.info("OrderPaymentEventPayload sent to Kafka for order id: {} and saga id: {}",
                    eventPayload.getOrderId(), sagaId);
        } catch (Exception ex) {
            log.error("Error while sending OrderPaymentEventPayload to Kafka with order id: {} and saga id: {}, error: {}",
                    eventPayload.getOrderId(), sagaId, ex.getMessage());
        }
    }

    private OrderPaymentEventPayload getOrderPaymentEventPayload(String payload) {
        try {
            return objectMapper.readValue(payload, OrderPaymentEventPayload.class);
        } catch (JsonProcessingException e) {
            String errorMsg = "Could not read OrderDomainEventPayload!";
            log.error(errorMsg, e);
            throw new OrderDomainException(errorMsg, e);
        }
    }
}
