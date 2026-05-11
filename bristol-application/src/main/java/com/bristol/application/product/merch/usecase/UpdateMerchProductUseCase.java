package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.application.product.merch.dto.UpdateMerchProductRequest;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to update a merch product.
 */
@Service
@RequiredArgsConstructor
public class UpdateMerchProductUseCase {

    private final MerchProductRepository merchProductRepository;
    private final MerchTypeRepository merchTypeRepository;
    private final MerchProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public MerchProductDto execute(String id, UpdateMerchProductRequest request) {
        // Find existing product
        ProductId productId = new ProductId(UUID.fromString(id));
        MerchProduct product = merchProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("MerchProduct", id));

        // Validate merch type exists
        MerchTypeId merchTypeId = new MerchTypeId(UUID.fromString(request.getMerchTypeId()));
        if (merchTypeRepository.findById(merchTypeId).isEmpty()) {
            throw new NotFoundException("MerchType", request.getMerchTypeId());
        }

        // Update product
        MerchProduct updated = product.update(
                request.getName(),
                request.getDescription(),
                Money.of(request.getBasePrice()),
                merchTypeId,
                request.getMerchCategory(),
                request.getMaterial(),
                request.getBrand(),
                timeProvider.now()
        );

        // Save and return
        MerchProduct saved = merchProductRepository.save(updated);
        return mapper.toDto(saved);
    }
}
