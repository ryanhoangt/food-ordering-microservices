package com.foodorder.service.payment.messaging.mapper;

import com.foodorder.domain.valueobject.PaymentOrderStatus;
import com.foodorder.kafka.order.avro.model.PaymentRequestAvroModel;
import com.foodorder.kafka.order.avro.model.PaymentResponseAvroModel;
import com.foodorder.kafka.order.avro.model.PaymentStatus;
import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;
import com.foodorder.service.payment.domain.event.PaymentCancelledEvent;
import com.foodorder.service.payment.domain.event.PaymentCompletedEvent;
import com.foodorder.service.payment.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel fromPaymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(paymentCompletedEvent.getPayment().getId().getIdValue().toString())
                .setCustomerId(paymentCompletedEvent.getPayment().getCustomerId().getIdValue().toString())
                .setOrderId(paymentCompletedEvent.getPayment().getOrderId().getIdValue().toString())
                .setPrice(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCompletedEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel fromPaymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(paymentCancelledEvent.getPayment().getId().getIdValue().toString())
                .setCustomerId(paymentCancelledEvent.getPayment().getCustomerId().getIdValue().toString())
                .setOrderId(paymentCancelledEvent.getPayment().getOrderId().getIdValue().toString())
                .setPrice(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCancelledEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel fromPaymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(paymentFailedEvent.getPayment().getId().getIdValue().toString())
                .setCustomerId(paymentFailedEvent.getPayment().getCustomerId().getIdValue().toString())
                .setOrderId(paymentFailedEvent.getPayment().getOrderId().getIdValue().toString())
                .setPrice(paymentFailedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentFailedEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentFailedEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentFailedEvent.getFailureMessages())
                .build();
    }

    public PaymentRequestDTO fromPaymentRequestAvroModelToPaymentRequestDTO(PaymentRequestAvroModel avroModel) {
        return PaymentRequestDTO.builder()
                .id(avroModel.getId())
                .sagaId(avroModel.getSagaId())
                .customerId(avroModel.getCustomerId())
                .orderId(avroModel.getOrderId())
                .price(avroModel.getPrice())
                .createdAt(avroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(avroModel.getPaymentOrderStatus().name()))
                .build();
    }
}
