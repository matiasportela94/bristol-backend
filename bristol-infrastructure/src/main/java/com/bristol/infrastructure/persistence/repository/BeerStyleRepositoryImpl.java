package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleCategory;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.infrastructure.persistence.entity.BeerStyleEntity;
import com.bristol.infrastructure.persistence.mapper.BeerStyleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BeerStyleRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class BeerStyleRepositoryImpl implements BeerStyleRepository {

    private final JpaBeerStyleRepository jpaRepository;
    private final BeerStyleMapper mapper;

    @Override
    public BeerStyle save(BeerStyle beerStyle) {
        var entity = mapper.toEntity(beerStyle);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BeerStyle> findById(BeerStyleId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<BeerStyle> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<BeerStyle> findAll() {
        return jpaRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerStyle> findByCategory(BeerStyleCategory category) {
        var entityCategory = BeerStyleEntity.BeerStyleCategoryEnum.valueOf(category.name());
        return jpaRepository.findByCategory(entityCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeerStyle> findActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public void delete(BeerStyleId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
