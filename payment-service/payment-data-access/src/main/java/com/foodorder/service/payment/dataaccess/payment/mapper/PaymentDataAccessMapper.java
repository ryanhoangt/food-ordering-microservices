package com.foodorder.service.payment.dataaccess.payment.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.service.payment.dataaccess.payment.entity.PaymentEntity;
import com.foodorder.service.payment.domain.entity.Payment;
import com.foodorder.service.payment.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getIdValue())
                .customerId(payment.getCustomerId().getIdValue())
                .orderId(payment.getOrderId().getIdValue())
                .price(payment.getPrice().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.Builder.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }

}
