package com.bristol.domain.shared.valueobject;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money value object.
 * Represents an amount of money with proper arithmetic operations.
 */
@Getter
@EqualsAndHashCode
public class Money {

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("Money amount cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    /**
     * Add another money amount.
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Subtract another money amount.
     */
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    /**
     * Multiply by a factor.
     */
    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    /**
     * Multiply by a decimal factor.
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor));
    }

    /**
     * Calculate percentage.
     */
    public Money percentage(BigDecimal percent) {
        return new Money(this.amount.multiply(percent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
    }

    /**
     * Check if amount is positive.
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative.
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Check if amount is zero.
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if greater than another money amount.
     */
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Check if less than another money amount.
     */
    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    @Override
    public String toString() {
        return "$" + amount.toString();
    }
}
