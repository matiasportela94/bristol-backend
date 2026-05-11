package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.SpecialProduct;
import com.bristol.domain.product.SpecialProductRepository;
import com.bristol.infrastructure.persistence.mapper.SpecialProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SpecialProductRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class SpecialProductRepositoryImpl implements SpecialProductRepository {

    private final JpaSpecialProductRepository jpaRepository;
    private final SpecialProductMapper mapper;

    @Override
    public SpecialProduct save(SpecialProduct product) {
        var entity = mapper.toEntity(product);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecialProduct> findById(ProductId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<SpecialProduct> findAll() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findBySpecialType(SpecialTypeId specialTypeId) {
        return jpaRepository.findBySpecialTypeId(specialTypeId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findRequiringQuote() {
        return jpaRepository.findRequiringQuote().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findNotRequiringQuote() {
        return jpaRepository.findNotRequiringQuote().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findFeatured() {
        return jpaRepository.findFeatured().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findInStock() {
        return jpaRepository.findInStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> findLowStock() {
        return jpaRepository.findLowStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialProduct> searchByName(String name) {
        return jpaRepository.searchByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
