package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.infrastructure.persistence.entity.BeerStyleEntity;
import com.bristol.infrastructure.persistence.mapper.BeerProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BeerProductRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class BeerProductRepositoryImpl implements BeerProductRepository {

    private final JpaBeerProductRepository jpaRepository;
    private final BeerProductMapper mapper;

    @Override
    public BeerProduct save(BeerProduct product) {
        var entity = mapper.toEntity(product);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BeerProduct> findById(ProductId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<BeerProduct> findAll() {
        return jpaRepository.findAllActive().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findByBeerStyle(BeerStyleId beerStyleId) {
        return jpaRepository.findByBeerStyleId(beerStyleId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findByCategory(BeerStyleCategory category) {
        var entityCategory = BeerStyleEntity.BeerStyleCategoryEnum.valueOf(category.name());
        return jpaRepository.findByBeerStyleCategory(entityCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findFeatured() {
        return jpaRepository.findFeatured().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findInStock() {
        return jpaRepository.findInStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findLowStock() {
        return jpaRepository.findLowStock().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> searchByName(String name) {
        return jpaRepository.searchByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerProduct> findByBrewery(String brewery) {
        return jpaRepository.findByBrewery(brewery).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
