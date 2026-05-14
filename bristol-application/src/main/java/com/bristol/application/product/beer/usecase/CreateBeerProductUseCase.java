package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.CreateBeerProductRequest;
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

        return mapper.toDto(beerProductRepository.save(product));
    }
}
