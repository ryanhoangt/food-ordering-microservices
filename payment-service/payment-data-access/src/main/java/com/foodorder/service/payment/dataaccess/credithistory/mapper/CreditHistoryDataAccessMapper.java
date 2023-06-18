package com.foodorder.service.payment.dataaccess.credithistory.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.service.payment.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.foodorder.service.payment.domain.entity.CreditHistory;
import com.foodorder.service.payment.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.Builder.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getIdValue())
                .customerId(creditHistory.getCustomerId().getIdValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

}
