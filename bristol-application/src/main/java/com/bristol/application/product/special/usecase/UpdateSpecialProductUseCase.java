package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.application.product.special.dto.UpdateSpecialProductRequest;
import com.bristol.application.product.service.ProductPriceHistoryService;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.SpecialProduct;
import com.bristol.domain.product.SpecialProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to update a special product.
 */
@Service
@RequiredArgsConstructor
public class UpdateSpecialProductUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialTypeRepository specialTypeRepository;
    private final ProductPriceHistoryService priceHistoryService;
    private final SpecialProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialProductDto execute(String id, UpdateSpecialProductRequest request) {
        // Find existing product
        ProductId productId = new ProductId(UUID.fromString(id));
        SpecialProduct product = specialProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("SpecialProduct", id));

        // Validate special type exists
        SpecialTypeId specialTypeId = new SpecialTypeId(UUID.fromString(request.getSpecialTypeId()));
        if (specialTypeRepository.findById(specialTypeId).isEmpty()) {
            throw new NotFoundException("SpecialType", request.getSpecialTypeId());
        }

        Money newPrice = request.getBasePrice() != null ? Money.of(request.getBasePrice()) : null;
        priceHistoryService.recordIfChanged(productId, product.getBasePrice(), newPrice, timeProvider.now());

        SpecialProduct updated = product.update(
                request.getName(),
                request.getDescription(),
                newPrice,
                specialTypeId,
                request.getNotes(),
                request.getRequiresQuote(),
                timeProvider.now()
        );

        // Save and return
        SpecialProduct saved = specialProductRepository.save(updated);
        return mapper.toDto(saved);
    }
}
