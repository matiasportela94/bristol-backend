package com.bristol.application.product.special.usecase;

import com.bristol.application.product.special.dto.CreateFullSpecialProductRequest;
import com.bristol.application.product.special.dto.SpecialProductDto;
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
public class CreateFullSpecialProductUseCase {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialTypeRepository specialTypeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageService productImageService;
    private final SpecialProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public SpecialProductDto execute(CreateFullSpecialProductRequest request) {
        var p = request.getProduct();

        SpecialTypeId specialTypeId = new SpecialTypeId(UUID.fromString(p.getSpecialTypeId()));
        if (specialTypeRepository.findById(specialTypeId).isEmpty()) {
            throw new NotFoundException("SpecialType", p.getSpecialTypeId());
        }

        SpecialProduct saved = specialProductRepository.save(SpecialProduct.create(
                p.getName(), p.getDescription(),
                p.getBasePrice() != null ? Money.of(p.getBasePrice()) : null,
                specialTypeId, p.getNotes(), p.getRequiresQuote(),
                p.getStockQuantity(), p.getLowStockThreshold(), timeProvider.now()
        ));

        saveVariants(saved.getId(), request.getVariants());
        productImageService.createImages(saved.getId(), request.getImages());

        return mapper.toDto(saved);
    }

    private void saveVariants(ProductId productId, List<EmbeddedVariantRequest> variants) {
        if (variants == null || variants.isEmpty()) return;
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
