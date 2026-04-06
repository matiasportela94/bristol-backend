package com.bristol.domain.product;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for PriceHistory.
 */
public interface PriceHistoryRepository {

    PriceHistory save(PriceHistory history);

    Optional<PriceHistory> findById(PriceHistoryId id);

    List<PriceHistory> findByProductId(ProductId productId);

    List<PriceHistory> findByProductIdOrderByDate(ProductId productId);
}
