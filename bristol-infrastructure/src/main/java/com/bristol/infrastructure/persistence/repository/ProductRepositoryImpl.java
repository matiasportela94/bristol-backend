package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.infrastructure.persistence.entity.ProductEntity;
import com.bristol.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        var entityCategory = ProductEntity.ProductCategoryEnum.valueOf(category.name());
        return jpaRepository.findByCategoryAndIsActiveTrueAndDeletedAtIsNull(entityCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findFeatured() {
        // For now, return all active products. This can be enhanced later
        return jpaRepository.findByIsActiveTrueAndDeletedAtIsNull().stream()
                .map(mapper::toDomain)
                .filter(Product::isFeatured)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findInStock() {
        return jpaRepository.findInStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStock() {
        return jpaRepository.findLowStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByBeerType(BeerType beerType) {
        var entityBeerType = ProductEntity.BeerTypeEnum.valueOf(beerType.name());
        return jpaRepository.findByBeerTypeAndIsActiveTrueAndDeletedAtIsNull(entityBeerType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByName(String name) {
        return jpaRepository.searchByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
