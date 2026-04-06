package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.infrastructure.persistence.mapper.ProductVariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductVariantRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class ProductVariantRepositoryImpl implements ProductVariantRepository {

    private final JpaProductVariantRepository jpaRepository;
    private final ProductVariantMapper mapper;

    @Override
    public ProductVariant save(ProductVariant variant) {
        var entity = mapper.toEntity(variant);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProductVariant> findById(ProductVariantId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ProductVariant> findBySku(String sku) {
        return jpaRepository.findBySku(sku)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductVariant> findByProductId(ProductId productId) {
        return jpaRepository.findByProductId(productId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariant> findInStockByProductId(ProductId productId) {
        return jpaRepository.findInStockByProductId(productId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ProductVariantId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
