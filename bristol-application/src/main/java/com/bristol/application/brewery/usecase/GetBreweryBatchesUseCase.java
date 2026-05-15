package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.BreweryBatchDto;
import com.bristol.domain.brewery.BreweryBatchRepository;
import com.bristol.domain.catalog.BeerStyleId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBreweryBatchesUseCase {

    private final BreweryBatchRepository batchRepository;
    private final BreweryApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<BreweryBatchDto> execute() {
        return batchRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BreweryBatchDto> executeByStyle(UUID beerStyleId) {
        return batchRepository.findByBeerStyleId(new BeerStyleId(beerStyleId))
                .stream().map(mapper::toDto).toList();
    }
}
