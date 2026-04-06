package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * PriceHistory entity.
 * Records price changes for a product over time.
 * Typically created automatically by database trigger.
 */
@Getter
@Builder(toBuilder = true)
public class PriceHistory {

    private final PriceHistoryId id;
    private final ProductId productId;
    private final Money oldPrice;
    private final Money newPrice;
    private final UserId changedBy;
    private final Instant changedAt;

    /**
     * Factory method to create a new price history record.
     */
    public static PriceHistory create(
            ProductId productId,
            Money oldPrice,
            Money newPrice,
            UserId changedBy,
            Instant changedAt
    ) {
        return PriceHistory.builder()
                .id(PriceHistoryId.generate())
                .productId(productId)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changedBy(changedBy)
                .changedAt(changedAt)
                .build();
    }

    /**
     * Calculate price change amount.
     */
    public Money getPriceChange() {
        return newPrice.subtract(oldPrice);
    }

    /**
     * Check if price increased.
     */
    public boolean isPriceIncrease() {
        return getPriceChange().isPositive();
    }

    /**
     * Check if price decreased.
     */
    public boolean isPriceDecrease() {
        return getPriceChange().isLessThan(Money.zero());
    }
}
