package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.CreateMerchProductRequest;
import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to create a new merch product.
 */
@Service
@RequiredArgsConstructor
public class CreateMerchProductUseCase {

    private final MerchProductRepository merchProductRepository;
    private final MerchTypeRepository merchTypeRepository;
    private final MerchProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public MerchProductDto execute(CreateMerchProductRequest request) {
        // Validate merch type exists
        MerchTypeId merchTypeId = new MerchTypeId(UUID.fromString(request.getMerchTypeId()));
        if (merchTypeRepository.findById(merchTypeId).isEmpty()) {
            throw new NotFoundException("MerchType", request.getMerchTypeId());
        }

        // Create merch product
        MerchProduct product = MerchProduct.create(
                request.getName(),
                request.getDescription(),
                Money.of(request.getBasePrice()),
                merchTypeId,
                request.getMerchCategory(),
                request.getMaterial(),
                request.getBrand(),
                request.getStockQuantity(),
                request.getLowStockThreshold(),
                timeProvider.now()
        );

        // Save and return
        MerchProduct saved = merchProductRepository.save(product);
        return mapper.toDto(saved);
    }
}
