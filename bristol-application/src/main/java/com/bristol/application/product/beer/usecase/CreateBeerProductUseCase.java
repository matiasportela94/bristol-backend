package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.CreateBeerProductRequest;
import com.bristol.application.product.service.StockSyncService;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateBeerProductUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final BreweryInventoryRepository breweryInventoryRepository;
    private final StockSyncService stockSyncService;
    private final BeerProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerProductDto execute(CreateBeerProductRequest request) {
        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(request.getBeerStyleId()));
        if (beerStyleRepository.findById(beerStyleId).isEmpty()) {
            throw new NotFoundException("BeerStyle", request.getBeerStyleId());
        }

        BeerProduct product = BeerProduct.create(
                request.getName(),
                request.getDescription(),
                Money.of(request.getBasePrice()),
                beerStyleId,
                request.getOrigin(),
                request.getBrewery(),
                0,
                request.getLowStockThreshold(),
                request.getCansPerUnit(),
                timeProvider.now()
        );

        BeerProduct saved = beerProductRepository.save(product);

        // Sync stock from existing brewery inventory if available
        breweryInventoryRepository.findByBeerStyleId(beerStyleId)
                .ifPresent(inv -> stockSyncService.syncBeerStock(beerStyleId, inv.getTotalCans()));

        // Re-fetch to return updated stockQuantity after sync
        return mapper.toDto(beerProductRepository.findById(saved.getId()).orElse(saved));
    }
}
