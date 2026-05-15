package com.bristol.domain.product;

import java.util.List;

public interface ProductPriceHistoryRepository {
    ProductPriceHistory save(ProductPriceHistory entry);
    List<ProductPriceHistory> findByProductId(ProductId productId);
}
