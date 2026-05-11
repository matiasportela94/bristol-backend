package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.domain.catalog.BeerStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all beer styles (including inactive).
 */
@Service
@RequiredArgsConstructor
public class GetAllBeerStylesUseCase {

    private final BeerStyleRepository beerStyleRepository;
    private final BeerStyleApplicationMapper mapper;

    @Cacheable("beerStyles")
    @Transactional(readOnly = true)
    public List<BeerStyleDto> execute() {
        return beerStyleRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
