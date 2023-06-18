package com.foodorder.service.payment.dataaccess.creditentry.adapter;

import com.foodorder.domain.valueobject.CustomerId;
import com.foodorder.service.payment.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.foodorder.service.payment.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.foodorder.service.payment.domain.entity.CreditEntry;
import com.foodorder.service.payment.domain.port.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataAccessMapper creditEntryDataAccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper
                .creditEntryEntityToCreditEntry(creditEntryJpaRepository
                        .save(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository
                .findByCustomerId(customerId.getIdValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
