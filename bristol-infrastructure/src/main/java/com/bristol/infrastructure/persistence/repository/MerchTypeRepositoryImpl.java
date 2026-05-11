package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchType;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.infrastructure.persistence.entity.MerchTypeEntity;
import com.bristol.infrastructure.persistence.mapper.MerchTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MerchTypeRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class MerchTypeRepositoryImpl implements MerchTypeRepository {

    private final JpaMerchTypeRepository jpaRepository;
    private final MerchTypeMapper mapper;

    @Override
    public MerchType save(MerchType merchType) {
        var entity = mapper.toEntity(merchType);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MerchType> findById(MerchTypeId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<MerchType> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<MerchType> findAll() {
        return jpaRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchType> findByCategory(MerchCategory category) {
        var entityCategory = MerchTypeEntity.MerchCategoryEnum.valueOf(category.name());
        return jpaRepository.findByCategory(entityCategory).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchType> findActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public void delete(MerchTypeId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
