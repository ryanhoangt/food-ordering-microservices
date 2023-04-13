package com.foodorder.domain.valueobject;

import java.util.Objects;

public abstract class BaseId<T> {

    private final T idValue;

    protected BaseId(T idValue) {
        this.idValue = idValue;
    }

    public T getIdValue() {
        return idValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseId<?> baseId = (BaseId<?>) o;
        return idValue.equals(baseId.idValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idValue);
    }
}
