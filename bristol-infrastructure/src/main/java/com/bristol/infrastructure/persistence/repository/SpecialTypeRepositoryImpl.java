package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.SpecialType;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.infrastructure.persistence.mapper.SpecialTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SpecialTypeRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class SpecialTypeRepositoryImpl implements SpecialTypeRepository {

    private final JpaSpecialTypeRepository jpaRepository;
    private final SpecialTypeMapper mapper;

    @Override
    public SpecialType save(SpecialType specialType) {
        var entity = mapper.toEntity(specialType);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecialType> findById(SpecialTypeId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<SpecialType> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<SpecialType> findAll() {
        return jpaRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialType> findActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public void delete(SpecialTypeId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
