package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.CreateSpecialProductRequest;
import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
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
 * Use case to create a new special product.
 */
@Service
@RequiredArgsConstructor
public class CreateSpecialProductUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialTypeRepository specialTypeRepository;
    private final SpecialProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialProductDto execute(CreateSpecialProductRequest request) {
        // Validate special type exists
        SpecialTypeId specialTypeId = new SpecialTypeId(UUID.fromString(request.getSpecialTypeId()));
        if (specialTypeRepository.findById(specialTypeId).isEmpty()) {
            throw new NotFoundException("SpecialType", request.getSpecialTypeId());
        }

        // Create special product
        SpecialProduct product = SpecialProduct.create(
                request.getName(),
                request.getDescription(),
                request.getBasePrice() != null ? Money.of(request.getBasePrice()) : null,
                specialTypeId,
                request.getNotes(),
                request.getRequiresQuote(),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                timeProvider.now()
        );

        // Save and return
        SpecialProduct saved = specialProductRepository.save(product);
        return mapper.toDto(saved);
    }
}
