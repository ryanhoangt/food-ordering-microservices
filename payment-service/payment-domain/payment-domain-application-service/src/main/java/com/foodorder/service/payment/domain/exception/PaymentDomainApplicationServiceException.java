package com.foodorder.service.payment.domain.exception;

import com.foodorder.domain.exception.DomainException;

public class PaymentDomainApplicationServiceException extends DomainException {

    public PaymentDomainApplicationServiceException(String message) {
        super(message);
    }

    public PaymentDomainApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
