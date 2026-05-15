package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.UpdateBeerProductRequest;
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

/**
 * Use case to update a beer product.
 */
@Service
@RequiredArgsConstructor
public class UpdateBeerProductUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final BeerProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerProductDto execute(String id, UpdateBeerProductRequest request) {
        // Find existing product
        ProductId productId = new ProductId(UUID.fromString(id));
        BeerProduct product = beerProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("BeerProduct", id));

        // Validate beer style exists
        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(request.getBeerStyleId()));
        if (beerStyleRepository.findById(beerStyleId).isEmpty()) {
            throw new NotFoundException("BeerStyle", request.getBeerStyleId());
        }

        // Update product
        BeerProduct updated = product.update(
                request.getName(),
                request.getDescription(),
                Money.of(request.getBasePrice()),
                beerStyleId,
                request.getOrigin(),
                request.getBrewery(),
                request.getCansPerUnit(),
                timeProvider.now()
        );

        // Save and return
        BeerProduct saved = beerProductRepository.save(updated);
        return mapper.toDto(saved);
    }
}
