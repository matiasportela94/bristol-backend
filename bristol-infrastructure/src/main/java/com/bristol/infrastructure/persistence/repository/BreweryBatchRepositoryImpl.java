package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.brewery.BreweryBatch;
import com.bristol.domain.brewery.BreweryBatchRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.infrastructure.persistence.mapper.BreweryBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BreweryBatchRepositoryImpl implements BreweryBatchRepository {

    private final JpaBreweryBatchRepository jpaRepository;
    private final BreweryBatchMapper mapper;

    @Override
    public BreweryBatch save(BreweryBatch batch) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(batch)));
    }

    @Override
    public List<BreweryBatch> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<BreweryBatch> findByBeerStyleId(BeerStyleId beerStyleId) {
        return jpaRepository.findByBeerStyleIdOrderByCreatedAtDesc(beerStyleId.getValue())
                .stream().map(mapper::toDomain).toList();
    }
}
