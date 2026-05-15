package com.bristol.domain.product;

import com.bristol.domain.shared.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ProductPriceHistory {

    private final ProductPriceHistoryId id;
    private final ProductId productId;
    private final Money oldPrice;
    private final Money newPrice;
    private final Instant changedAt;

    public static ProductPriceHistory record(ProductId productId, Money oldPrice, Money newPrice, Instant now) {
        return ProductPriceHistory.builder()
                .id(ProductPriceHistoryId.generate())
                .productId(productId)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changedAt(now)
                .build();
    }
}
