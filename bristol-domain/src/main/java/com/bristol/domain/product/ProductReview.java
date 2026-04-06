package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Rating;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * ProductReview entity.
 * Represents a customer review and rating for a product.
 */
@Getter
@Builder(toBuilder = true)
public class ProductReview {

    private final ProductReviewId id;
    private final ProductId productId;
    private final UserId userId;
    private final Rating rating;
    private final String comment;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new product review.
     */
    public static ProductReview create(
            ProductId productId,
            UserId userId,
            Rating rating,
            String comment,
            Instant now
    ) {
        validateComment(comment);

        return ProductReview.builder()
                .id(ProductReviewId.generate())
                .productId(productId)
                .userId(userId)
                .rating(rating)
                .comment(comment)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update review.
     */
    public ProductReview update(Rating newRating, String newComment, Instant now) {
        validateComment(newComment);

        return this.toBuilder()
                .rating(newRating)
                .comment(newComment)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if review has a comment.
     */
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }

    private static void validateComment(String comment) {
        if (comment != null && comment.length() > 1000) {
            throw new ValidationException("Comment cannot exceed 1000 characters");
        }
    }
}
