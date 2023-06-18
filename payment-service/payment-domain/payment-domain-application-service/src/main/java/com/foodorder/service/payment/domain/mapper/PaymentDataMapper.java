package com.foodorder.service.payment.domain.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.domain.valueobject.OrderId;
import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;
import com.foodorder.service.payment.domain.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment fromRequestDTOToPayment(PaymentRequestDTO requestDTO) {
        return Payment.Builder.builder()
                .orderId(new OrderId(UUID.fromString(requestDTO.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(requestDTO.getCustomerId())))
                .price(new Money(requestDTO.getPrice()))
                .build();
    }
}
