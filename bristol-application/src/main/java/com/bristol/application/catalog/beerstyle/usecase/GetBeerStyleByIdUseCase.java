package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a beer style by ID.
 */
@Service
@RequiredArgsConstructor
public class GetBeerStyleByIdUseCase {

    private final BeerStyleRepository beerStyleRepository;
    private final BeerStyleApplicationMapper mapper;

    @Transactional(readOnly = true)
    public BeerStyleDto execute(UUID id) {
        return beerStyleRepository.findById(new BeerStyleId(id))
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("BeerStyle", id.toString()));
    }
}
