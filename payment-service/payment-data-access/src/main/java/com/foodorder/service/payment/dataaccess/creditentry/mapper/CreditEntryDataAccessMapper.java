package com.foodorder.service.payment.dataaccess.creditentry.mapper;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.domain.valueobject.Money;
import com.foodorder.service.payment.dataaccess.creditentry.entity.CreditEntryEntity;
import com.foodorder.service.payment.domain.entity.CreditEntry;
import com.foodorder.service.payment.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.Builder.builder()
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getIdValue())
                .customerId(creditEntry.getCustomerId().getIdValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

}
