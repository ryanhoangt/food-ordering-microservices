package com.foodorder.service.order.domain;

import com.foodorder.service.order.domain.dto.message.PaymentResponseDTO;
import com.foodorder.service.order.domain.event.OrderPaidEvent;
import com.foodorder.service.order.domain.port.input.message.listener.payment.PaymentResponseMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.foodorder.service.order.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class PaymentResponseMsgListenerImpl implements PaymentResponseMsgListener {

    private final OrderPaymentSaga orderPaymentSaga;

    public PaymentResponseMsgListenerImpl(OrderPaymentSaga orderPaymentSaga) {
        this.orderPaymentSaga = orderPaymentSaga;
    }

    @Override
    public void paymentCompletedHandler(PaymentResponseDTO responseDTO) {
        orderPaymentSaga.process(responseDTO);
        log.info("Order Payment Saga process operation is completed for order id: {}", responseDTO.getOrderId());
    }

    @Override
    public void paymentCancelledHandler(PaymentResponseDTO responseDTO) {
        orderPaymentSaga.rollback(responseDTO);
        log.info("Order is roll-backed for order id: {} with failure messages: {}",
                responseDTO.getPaymentId(),
                String.join(FAILURE_MESSAGE_DELIMITER));
    }
}
