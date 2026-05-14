package com.bristol.application.product.merch.usecase;

import com.bristol.application.product.merch.dto.CreateFullMerchProductRequest;
import com.bristol.application.product.merch.dto.MerchProductDto;
import com.bristol.application.product.usecase.ProductImageService;
import com.bristol.application.productvariant.dto.EmbeddedVariantRequest;
import com.bristol.domain.catalog.MerchTypeId;
import com.bristol.domain.catalog.MerchTypeRepository;
import com.bristol.domain.product.MerchProduct;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
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
public class CreateFullMerchProductUseCase {

    private final MerchProductRepository merchProductRepository;
    private final MerchTypeRepository merchTypeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageService productImageService;
    private final MerchProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public MerchProductDto execute(CreateFullMerchProductRequest request) {
        var p = request.getProduct();

        MerchTypeId merchTypeId = new MerchTypeId(UUID.fromString(p.getMerchTypeId()));
        if (merchTypeRepository.findById(merchTypeId).isEmpty()) {
            throw new NotFoundException("MerchType", p.getMerchTypeId());
        }

        MerchProduct saved = merchProductRepository.save(MerchProduct.create(
                p.getName(), p.getDescription(), Money.of(p.getBasePrice()),
                merchTypeId, p.getMerchCategory(), p.getMaterial(), p.getBrand(),
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
