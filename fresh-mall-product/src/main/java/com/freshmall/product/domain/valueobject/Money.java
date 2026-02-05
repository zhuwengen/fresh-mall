package com.freshmall.product.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额值对象
 * 不可变对象，所有操作返回新实例
 */
@Getter
@EqualsAndHashCode
public class Money implements Serializable {
    
    private static final String DEFAULT_CURRENCY = "CNY";
    private static final int SCALE = 2;
    
    private final BigDecimal amount;
    private final String currency;
    
    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        this.currency = currency == null ? DEFAULT_CURRENCY : currency;
    }
    
    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }
    
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }
    
    public Money add(Money other) {
        checkCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    public boolean greaterThan(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public boolean lessThan(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }
    
    public boolean greaterThanOrEqual(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    public boolean lessThanOrEqual(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount) <= 0;
    }
    
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    private void checkCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("货币类型不匹配");
        }
    }
    
    @Override
    public String toString() {
        return currency + " " + amount;
    }
}
