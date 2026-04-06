package com.bristol.domain.product;

import com.bristol.domain.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ProductReview.
 */
public interface ProductReviewRepository {

    ProductReview save(ProductReview review);

    Optional<ProductReview> findById(ProductReviewId id);

    Optional<ProductReview> findByProductAndUser(ProductId productId, UserId userId);

    List<ProductReview> findByProductId(ProductId productId);

    List<ProductReview> findByUserId(UserId userId);

    /**
     * Calculate average rating for a product.
     */
    double getAverageRating(ProductId productId);

    /**
     * Get total number of reviews for a product.
     */
    long getReviewCount(ProductId productId);

    void delete(ProductReviewId id);
}
