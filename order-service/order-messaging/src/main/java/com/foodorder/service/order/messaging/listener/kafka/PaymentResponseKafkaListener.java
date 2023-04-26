package com.foodorder.service.order.messaging.listener.kafka;

import com.foodorder.kafka.consumer.KafkaConsumer;
import com.foodorder.kafka.order.avro.model.PaymentResponseAvroModel;
import com.foodorder.kafka.order.avro.model.PaymentStatus;
import com.foodorder.service.order.domain.port.input.message.listener.payment.PaymentResponseMsgListener;
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
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMsgListener paymentResponseMsgListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(PaymentResponseMsgListener paymentResponseMsgListener,
                                        OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMsgListener = paymentResponseMsgListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(responseModel -> {
            if (responseModel.getPaymentStatus() == PaymentStatus.COMPLETED) {
                log.info("Processing successful payment for order id: {}", responseModel.getOrderId());
                paymentResponseMsgListener.paymentCompletedHandler(
                        orderMessagingDataMapper.fromPaymentResponseAvroModelToPaymentResponseDTO(responseModel)
                );
            } else if (responseModel.getPaymentStatus() == PaymentStatus.CANCELLED ||
                    responseModel.getPaymentStatus() == PaymentStatus.FAILED) {
                log.info("Processing unsuccessful payment for order id: {}", responseModel.getOrderId());
                paymentResponseMsgListener.paymentCancelledHandler(
                        orderMessagingDataMapper.fromPaymentResponseAvroModelToPaymentResponseDTO(responseModel)
                );
            }
        });
    }

}
