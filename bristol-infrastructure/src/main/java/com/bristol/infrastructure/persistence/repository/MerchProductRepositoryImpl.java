package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.infrastructure.persistence.entity.MerchProductEntity.MerchCategoryEnum;
import com.bristol.infrastructure.persistence.mapper.MerchProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MerchProductRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class MerchProductRepositoryImpl implements MerchProductRepository {

    private final JpaMerchProductRepository jpaRepository;
    private final MerchProductMapper mapper;

    @Override
    public MerchProduct save(MerchProduct product) {
        var entity = mapper.toEntity(product);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MerchProduct> findById(ProductId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<MerchProduct> findAll() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findByMerchType(MerchTypeId merchTypeId) {
        return jpaRepository.findByMerchTypeId(merchTypeId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findByCategory(MerchCategory category) {
        var entityCategory = MerchCategoryEnum.valueOf(category.name());
        return jpaRepository.findByMerchCategory(entityCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findFeatured() {
        return jpaRepository.findFeatured().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findInStock() {
        return jpaRepository.findInStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findLowStock() {
        return jpaRepository.findLowStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> searchByName(String name) {
        return jpaRepository.searchByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchProduct> findByBrand(String brand) {
        return jpaRepository.findByBrand(brand).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
