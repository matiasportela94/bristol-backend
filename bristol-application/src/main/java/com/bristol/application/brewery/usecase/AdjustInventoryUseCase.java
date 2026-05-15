package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.AdjustInventoryRequest;
import com.bristol.application.brewery.dto.BreweryInventoryDto;
import com.bristol.application.brewery.service.BreweryStockMapService;
import com.bristol.application.product.service.StockSyncService;
import com.bristol.domain.brewery.BreweryInventoryMovement;
import com.bristol.domain.brewery.BreweryInventoryMovementRepository;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.brewery.MovementType;
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
    private final BreweryInventoryMovementRepository movementRepository;
    private final StockSyncService stockSyncService;
    private final BreweryStockMapService breweryStockMapService;
    private final BreweryApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BreweryInventoryDto execute(UUID beerStyleId, AdjustInventoryRequest request) {
        var styleId = new BeerStyleId(beerStyleId);
        var inventory = inventoryRepository.findByBeerStyleId(styleId)
                .orElseThrow(() -> new NotFoundException("BreweryInventory", beerStyleId.toString()));

        var now = timeProvider.now();
        var adjusted = inventoryRepository.save(inventory.adjustTo(request.getNewTotal(), now));

        movementRepository.save(BreweryInventoryMovement.create(
                styleId, MovementType.ADJUSTMENT,
                adjusted.getTotalCans() - inventory.getTotalCans(),
                inventory.getTotalCans(), adjusted.getTotalCans(),
                null, null, request.getReason(), now));

        stockSyncService.syncBeerStock(styleId, adjusted.getTotalCans());
        breweryStockMapService.evict();
        return mapper.toDto(adjusted);
    }
}
