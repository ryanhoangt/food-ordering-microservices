package com.foodorder.service.payment.messaging.publisher.kafka;

import com.foodorder.kafka.order.avro.model.PaymentResponseAvroModel;
import com.foodorder.kafka.producer.KafkaMsgHelper;
import com.foodorder.kafka.producer.service.KafkaProducer;
import com.foodorder.service.payment.domain.config.PaymentServiceConfigData;
import com.foodorder.service.payment.domain.event.PaymentCompletedEvent;
import com.foodorder.service.payment.domain.event.PaymentFailedEvent;
import com.foodorder.service.payment.domain.port.output.message.publisher.PaymentCompletedMsgPublisher;
import com.foodorder.service.payment.domain.port.output.message.publisher.PaymentFailedMsgPublisher;
import com.foodorder.service.payment.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentFailedKafkaMsgPublisher implements PaymentFailedMsgPublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMsgHelper kafkaMsgHelper;

    public PaymentFailedKafkaMsgPublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                          KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                          PaymentServiceConfigData paymentServiceConfigData,
                                          KafkaMsgHelper kafkaMsgHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMsgHelper = kafkaMsgHelper;
    }

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getIdValue().toString();

        log.info("Received PaymentFailedEvent for order id: {}", orderId);

        PaymentResponseAvroModel responseAvroModel =
                paymentMessagingDataMapper.fromPaymentFailedEventToPaymentResponseAvroModel(domainEvent);
        try {
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    responseAvroModel,
                    kafkaMsgHelper.getCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            responseAvroModel,
                            orderId,
                            "PaymentResponseAvroModel"
                    )
            );
            log.info("PaymentResponseAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message to Kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
