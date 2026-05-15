package com.bristol.application.product.service;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductPriceHistory;
import com.bristol.domain.product.ProductPriceHistoryRepository;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductPriceHistoryService {

    private final ProductPriceHistoryRepository repository;

    /**
     * Records a price change entry only if the price actually changed.
     * Both oldPrice and newPrice may be null (e.g. special products with requiresQuote).
     */
    public void recordIfChanged(ProductId productId, Money oldPrice, Money newPrice, Instant now) {
        BigDecimal oldAmount = oldPrice != null ? oldPrice.getAmount() : null;
        BigDecimal newAmount = newPrice != null ? newPrice.getAmount() : null;

        if (Objects.equals(oldAmount, newAmount)) return;

        repository.save(ProductPriceHistory.record(productId, oldPrice, newPrice, now));
    }
}
