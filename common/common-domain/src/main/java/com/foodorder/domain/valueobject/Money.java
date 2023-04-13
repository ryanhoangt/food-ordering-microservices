package com.foodorder.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO;
            return;
        }
        this.amount = amount;
    }

    public boolean isGreaterThanZero() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money otherM) {
        return this.amount.compareTo(otherM.getAmount()) > 0;
    }

    public Money add(Money otherM) {
        return new Money(setScale(this.amount.add(otherM.getAmount())));
    }

    public Money subtract(Money otherM) {
        return new Money(setScale(this.amount.subtract(otherM.getAmount())));
    }

    public Money multiply(Money otherM) {
        return new Money(setScale(this.amount.multiply(otherM.getAmount())));
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    private BigDecimal setScale(BigDecimal inputAmt) {
        return inputAmt.setScale(2, RoundingMode.HALF_EVEN);
    }
}
