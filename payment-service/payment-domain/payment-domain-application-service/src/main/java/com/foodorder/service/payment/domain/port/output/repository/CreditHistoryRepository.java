package com.foodorder.service.payment.domain.port.output.repository;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.service.payment.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
