package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductReview;
import com.bristol.domain.product.ProductReviewId;
import com.bristol.domain.product.ProductReviewRepository;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.mapper.ProductReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductReviewRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class ProductReviewRepositoryImpl implements ProductReviewRepository {

    private final JpaProductReviewRepository jpaRepository;
    private final ProductReviewMapper mapper;

    @Override
    public ProductReview save(ProductReview review) {
        var entity = mapper.toEntity(review);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProductReview> findById(ProductReviewId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductReview> findByProductId(ProductId productId) {
        return jpaRepository.findByProductId(productId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductReview> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductReview> findByProductAndUser(ProductId productId, UserId userId) {
        return jpaRepository.findByProductIdAndUserId(productId.getValue(), userId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public double getAverageRating(ProductId productId) {
        Double avg = jpaRepository.getAverageRating(productId.getValue());
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getReviewCount(ProductId productId) {
        return jpaRepository.countByProductId(productId.getValue());
    }

    @Override
    public void delete(ProductReviewId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
