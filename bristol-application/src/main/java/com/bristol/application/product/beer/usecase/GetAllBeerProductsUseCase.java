package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.domain.product.BeerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case to get all beer products.
 */
@Service
@RequiredArgsConstructor
public class GetAllBeerProductsUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerProductApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<BeerProductDto> execute() {
        return beerProductRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
