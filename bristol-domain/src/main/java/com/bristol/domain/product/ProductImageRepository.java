package com.bristol.domain.product;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ProductImage.
 */
public interface ProductImageRepository {

    ProductImage save(ProductImage image);

    Optional<ProductImage> findById(ProductImageId id);

    List<ProductImage> findByProductId(ProductId productId);

    Optional<ProductImage> findPrimaryByProductId(ProductId productId);

    void delete(ProductImageId id);

    void deleteAllByProductId(ProductId productId);
}
