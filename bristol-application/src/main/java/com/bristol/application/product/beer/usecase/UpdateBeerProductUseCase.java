package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.UpdateBeerProductRequest;
import com.bristol.application.product.service.ProductPriceHistoryService;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateBeerProductUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final ProductPriceHistoryService priceHistoryService;
    private final BeerProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerProductDto execute(String id, UpdateBeerProductRequest request) {
        ProductId productId = new ProductId(UUID.fromString(id));
        BeerProduct product = beerProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("BeerProduct", id));

        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(request.getBeerStyleId()));
        if (beerStyleRepository.findById(beerStyleId).isEmpty()) {
            throw new NotFoundException("BeerStyle", request.getBeerStyleId());
        }

        Money newPrice = Money.of(request.getBasePrice());
        priceHistoryService.recordIfChanged(productId, product.getBasePrice(), newPrice, timeProvider.now());

        BeerProduct updated = product.update(
                request.getName(),
                request.getDescription(),
                newPrice,
                beerStyleId,
                request.getOrigin(),
                request.getBrewery(),
                request.getCansPerUnit(),
                timeProvider.now()
        );

        BeerProduct saved = beerProductRepository.save(updated);
        return mapper.toDto(saved);
    }
}
