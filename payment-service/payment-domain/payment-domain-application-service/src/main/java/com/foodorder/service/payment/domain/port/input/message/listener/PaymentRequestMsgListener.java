package com.foodorder.service.payment.domain.port.input.message.listener;

import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;

public interface PaymentRequestMsgListener {

    void completePayment(PaymentRequestDTO requestDTO);

    void cancelPayment(PaymentRequestDTO requestDTO);
}
