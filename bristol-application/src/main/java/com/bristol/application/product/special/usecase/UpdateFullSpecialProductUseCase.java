package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.SpecialProductDto;
import com.bristol.application.product.special.dto.UpdateFullSpecialProductRequest;
import com.bristol.application.product.service.ProductPriceHistoryService;
import com.bristol.application.product.usecase.ProductImageService;
import com.bristol.application.productvariant.dto.EmbeddedVariantRequest;
import com.bristol.domain.catalog.SpecialTypeId;
import com.bristol.domain.catalog.SpecialTypeRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.product.SpecialProduct;
import com.bristol.domain.product.SpecialProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateFullSpecialProductUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialTypeRepository specialTypeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageService productImageService;
    private final ProductPriceHistoryService priceHistoryService;
    private final SpecialProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialProductDto execute(String id, UpdateFullSpecialProductRequest request) {
        ProductId productId = new ProductId(UUID.fromString(id));
        SpecialProduct product = specialProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("SpecialProduct", id));

        var p = request.getProduct();
        SpecialTypeId specialTypeId = new SpecialTypeId(UUID.fromString(p.getSpecialTypeId()));
        if (specialTypeRepository.findById(specialTypeId).isEmpty()) {
            throw new NotFoundException("SpecialType", p.getSpecialTypeId());
        }

        Money newPrice = p.getBasePrice() != null ? Money.of(p.getBasePrice()) : null;
        priceHistoryService.recordIfChanged(productId, product.getBasePrice(), newPrice, timeProvider.now());

        SpecialProduct saved = specialProductRepository.save(product.update(
                p.getName(), p.getDescription(),
                newPrice,
                specialTypeId, p.getNotes(), p.getRequiresQuote(), timeProvider.now()
        ));

        if (request.getVariants() != null) {
            replaceVariants(productId, request.getVariants());
        }
        productImageService.replaceImages(productId, request.getImages());

        return mapper.toDto(saved);
    }

    private void replaceVariants(ProductId productId, List<EmbeddedVariantRequest> variants) {
        productVariantRepository.findByProductId(productId)
                .forEach(v -> productVariantRepository.delete(v.getId()));
        for (EmbeddedVariantRequest v : variants) {
            productVariantRepository.save(ProductVariant.create(
                    productId, v.getSku(), v.getSize(), v.getColor(),
                    v.getAdditionalPrice() != null ? Money.of(v.getAdditionalPrice()) : Money.zero(),
                    v.getStockQuantity() != null ? v.getStockQuantity() : 0,
                    v.getImageUrl(), timeProvider.now()
            ));
        }
    }
}
