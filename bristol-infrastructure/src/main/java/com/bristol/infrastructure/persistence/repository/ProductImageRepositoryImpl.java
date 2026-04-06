package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductImage;
import com.bristol.domain.product.ProductImageId;
import com.bristol.domain.product.ProductImageRepository;
import com.bristol.infrastructure.persistence.mapper.ProductImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the product image repository port.
 */
@Component
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final JpaProductImageRepository jpaProductImageRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImage save(ProductImage image) {
        var saved = jpaProductImageRepository.save(productImageMapper.toEntity(image));
        return productImageMapper.toDomain(saved);
    }

    @Override
    public Optional<ProductImage> findById(ProductImageId id) {
        return jpaProductImageRepository.findById(id.getValue())
                .map(productImageMapper::toDomain);
    }

    @Override
    public List<ProductImage> findByProductId(ProductId productId) {
        return jpaProductImageRepository.findByProductIdOrderByDisplayOrderAscCreatedAtAsc(productId.getValue()).stream()
                .filter(entity -> entity.getImageData() != null)
                .map(productImageMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ProductImage> findPrimaryByProductId(ProductId productId) {
        return jpaProductImageRepository.findFirstByProductIdAndIsPrimaryTrue(productId.getValue())
                .filter(entity -> entity.getImageData() != null)
                .map(productImageMapper::toDomain);
    }

    @Override
    public void delete(ProductImageId id) {
        jpaProductImageRepository.deleteById(id.getValue());
    }

    @Override
    public void deleteAllByProductId(ProductId productId) {
        jpaProductImageRepository.deleteByProductId(productId.getValue());
    }
}
