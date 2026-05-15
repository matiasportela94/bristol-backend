package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.AddBrewingBatchRequest;
import com.bristol.application.brewery.dto.BreweryBatchDto;
import com.bristol.application.brewery.service.BreweryStockMapService;
import com.bristol.application.product.service.StockSyncService;
import com.bristol.domain.brewery.BreweryBatch;
import com.bristol.domain.brewery.BreweryBatchRepository;
import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddBrewingBatchUseCase {

    private final BreweryBatchRepository batchRepository;
    private final BreweryInventoryRepository inventoryRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final StockSyncService stockSyncService;
    private final BreweryStockMapService breweryStockMapService;
    private final BreweryApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BreweryBatchDto execute(AddBrewingBatchRequest request) {
        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(request.getBeerStyleId()));

        if (!beerStyleRepository.findById(beerStyleId).map(s -> s.isActive()).orElse(false)) {
            throw new NotFoundException("BeerStyle", request.getBeerStyleId());
        }

        Instant now = timeProvider.now();
        BreweryBatch batch = BreweryBatch.create(beerStyleId, request.getCansProduced(), request.getCanCapacityMl(), request.getNotes(), now);
        BreweryBatch saved = batchRepository.save(batch);

        BreweryInventory inventory = inventoryRepository.findByBeerStyleId(beerStyleId)
                .orElseGet(() -> BreweryInventory.create(beerStyleId, now));

        BreweryInventory updated = inventoryRepository.save(inventory.addCans(request.getCansProduced(), now));

        stockSyncService.syncBeerStock(beerStyleId, updated.getTotalCans());
        breweryStockMapService.evict();

        return mapper.toDto(saved);
    }
}
