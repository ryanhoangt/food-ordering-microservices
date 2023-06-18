package com.foodorder.service.payment.messaging.listener.kafka;

import com.foodorder.kafka.consumer.KafkaConsumer;
import com.foodorder.kafka.order.avro.model.PaymentOrderStatus;
import com.foodorder.kafka.order.avro.model.PaymentRequestAvroModel;
import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;
import com.foodorder.service.payment.domain.port.input.message.listener.PaymentRequestMsgListener;
import com.foodorder.service.payment.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMsgListener paymentRequestMsgListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMsgListener paymentRequestMsgListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMsgListener = paymentRequestMsgListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
                topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(requestAvroModel -> {
            if (requestAvroModel.getPaymentOrderStatus() == PaymentOrderStatus.PENDING) {
                log.info("Processing payment for order id: {}", requestAvroModel.getOrderId());
                paymentRequestMsgListener.completePayment(
                        paymentMessagingDataMapper.fromPaymentRequestAvroModelToPaymentRequestDTO(requestAvroModel)
                );
            } else if (requestAvroModel.getPaymentOrderStatus() == PaymentOrderStatus.CANCELLED) {
                log.info("Cancelling payment for order id: {}", requestAvroModel.getOrderId());
                paymentRequestMsgListener.cancelPayment(
                        paymentMessagingDataMapper.fromPaymentRequestAvroModelToPaymentRequestDTO(requestAvroModel)
                );
            }
        });
    }
}
