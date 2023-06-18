package com.foodorder.service.payment.domain.port.output.repository;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.service.payment.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
