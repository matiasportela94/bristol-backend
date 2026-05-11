package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to get a beer product by ID.
 */
@Service
@RequiredArgsConstructor
public class GetBeerProductByIdUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public BeerProductDto execute(String id) {
        ProductId productId = new ProductId(UUID.fromString(id));
        BeerProduct product = beerProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("BeerProduct", id));

        return mapper.toDto(product);
    }
}
