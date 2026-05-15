package com.bristol.application.product.beer.usecase;

import com.bristol.application.product.beer.dto.BeerProductDto;
import com.bristol.application.product.beer.dto.UpdateFullBeerProductRequest;
import com.bristol.application.product.usecase.ProductImageService;
import com.bristol.application.productvariant.dto.EmbeddedVariantRequest;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
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
public class UpdateFullBeerProductUseCase {

    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageService productImageService;
    private final BeerProductApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerProductDto execute(String id, UpdateFullBeerProductRequest request) {
        ProductId productId = new ProductId(UUID.fromString(id));
        BeerProduct product = beerProductRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("BeerProduct", id));

        var p = request.getProduct();
        BeerStyleId beerStyleId = new BeerStyleId(UUID.fromString(p.getBeerStyleId()));
        if (beerStyleRepository.findById(beerStyleId).isEmpty()) {
            throw new NotFoundException("BeerStyle", p.getBeerStyleId());
        }

        BeerProduct updated = product.update(
                p.getName(), p.getDescription(), Money.of(p.getBasePrice()),
                beerStyleId,
                p.getOrigin(), p.getBrewery(), p.getCansPerUnit(), timeProvider.now()
        );

        boolean hasNoVariants = request.getVariants() == null || request.getVariants().isEmpty();
        if (hasNoVariants && p.getStockQuantity() != null) {
            updated = updated.updateStock(p.getStockQuantity(), timeProvider.now());
        }
        if (p.getLowStockThreshold() != null) {
            updated = updated.toBuilder().lowStockThreshold(p.getLowStockThreshold()).build();
        }

        BeerProduct saved = beerProductRepository.save(updated);

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
