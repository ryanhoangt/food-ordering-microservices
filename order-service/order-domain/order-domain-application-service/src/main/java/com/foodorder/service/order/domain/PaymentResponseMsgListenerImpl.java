package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;
import com.foodorder.service.order.domain.port.input.message.listener.payment.PaymentResponseMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class PaymentResponseMsgListenerImpl implements PaymentResponseMsgListener {

    @Override
    public void paymentCompletedHandler(PaymentResponseDTO responseDTO) {

    }

    @Override
    public void paymentCancelledHandler(PaymentResponseDTO responseDTO) {

    }
}
