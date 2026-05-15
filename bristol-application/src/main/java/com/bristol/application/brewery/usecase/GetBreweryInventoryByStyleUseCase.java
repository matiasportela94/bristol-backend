package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.BreweryInventoryDto;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBreweryInventoryByStyleUseCase {

    private final BreweryInventoryRepository inventoryRepository;
    private final BreweryApplicationMapper mapper;

    @Transactional(readOnly = true)
    public BreweryInventoryDto execute(UUID beerStyleId) {
        return inventoryRepository.findByBeerStyleId(new BeerStyleId(beerStyleId))
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("BreweryInventory", beerStyleId.toString()));
    }
}
