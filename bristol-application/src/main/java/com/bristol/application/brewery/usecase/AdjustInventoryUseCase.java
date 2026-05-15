package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.AdjustInventoryRequest;
import com.bristol.application.brewery.dto.BreweryInventoryDto;
import com.bristol.application.brewery.service.BreweryStockMapService;
import com.bristol.application.product.service.StockSyncService;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdjustInventoryUseCase {

    private final BreweryInventoryRepository inventoryRepository;
    private final StockSyncService stockSyncService;
    private final BreweryStockMapService breweryStockMapService;
    private final BreweryApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BreweryInventoryDto execute(UUID beerStyleId, AdjustInventoryRequest request) {
        var styleId = new BeerStyleId(beerStyleId);
        var inventory = inventoryRepository.findByBeerStyleId(styleId)
                .orElseThrow(() -> new NotFoundException("BreweryInventory", beerStyleId.toString()));

        var adjusted = inventoryRepository.save(inventory.adjustTo(request.getNewTotal(), timeProvider.now()));
        stockSyncService.syncBeerStock(styleId, adjusted.getTotalCans());
        breweryStockMapService.evict();
        return mapper.toDto(adjusted);
    }
}
