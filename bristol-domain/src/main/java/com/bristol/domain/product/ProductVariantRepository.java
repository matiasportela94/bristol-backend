package com.bristol.domain.product;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ProductVariant.
 */
public interface ProductVariantRepository {

    ProductVariant save(ProductVariant variant);

    Optional<ProductVariant> findById(ProductVariantId id);

    Optional<ProductVariant> findBySku(String sku);

    List<ProductVariant> findByProductId(ProductId productId);

    List<ProductVariant> findInStockByProductId(ProductId productId);

    void delete(ProductVariantId id);
}
