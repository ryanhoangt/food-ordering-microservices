package com.foodorder.service.payment.domain.port;

import com.foodorder.service.payment.domain.dto.PaymentRequestDTO;
import com.foodorder.service.payment.domain.event.PaymentEvent;
import com.foodorder.service.payment.domain.port.input.message.listener.PaymentRequestMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMsgListenerImpl implements PaymentRequestMsgListener {

    private final PaymentRequestHelper paymentRequestHelper;

    public PaymentRequestMsgListenerImpl(PaymentRequestHelper paymentRequestHelper) {
        this.paymentRequestHelper = paymentRequestHelper;
    }

    @Override
    public void completePayment(PaymentRequestDTO requestDTO) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(requestDTO);
        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequestDTO requestDTO) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(requestDTO);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getIdValue(),
                paymentEvent.getPayment().getOrderId().getIdValue());

        paymentEvent.fire();
    }
}
