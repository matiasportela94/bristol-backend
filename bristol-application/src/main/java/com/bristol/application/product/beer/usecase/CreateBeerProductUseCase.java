package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.CreateBeerProductRequest;
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
 * Use case to create a new beer product.
 */
@Service
@RequiredArgsConstructor
public class CreateBeerProductUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final BeerProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerProductDto execute(CreateBeerProductRequest request) {
        // Validate beer style exists
        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(request.getBeerStyleId()));
        if (beerStyleRepository.findById(beerStyleId).isEmpty()) {
            throw new NotFoundException("BeerStyle", request.getBeerStyleId());
        }

        // Create beer product
        BeerProduct product = BeerProduct.create(
                request.getName(),
                request.getDescription(),
                Money.of(request.getBasePrice()),
                beerStyleId,
                request.getBeerCategory(),
                request.getAbv(),
                request.getIbu(),
                request.getSrm(),
                request.getOrigin(),
                request.getBrewery(),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                timeProvider.now()
        );

        // Save and return
        BeerProduct saved = beerProductRepository.save(product);
        return mapper.toDto(saved);
    }
}
