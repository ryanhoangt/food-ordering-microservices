package com.foodorder.service.order.domain.port.input.message.listener.payment;

import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;

public interface PaymentResponseMsgListener {

    void paymentCompletedHandler(PaymentResponseDTO responseDTO);

    void paymentCancelledHandler(PaymentResponseDTO responseDTO);
}
