package com.bristol.application.product.usecase;

import com.bristol.application.productvariant.dto.ProductVariantDto;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantCatalogService {

    private final ProductVariantRepository productVariantRepository;
    private final com.bristol.application.productvariant.usecase.ProductVariantMapper productVariantMapper;

    public List<ProductVariantDto> getVariants(ProductId productId) {
        return productVariantRepository.findByProductId(productId).stream()
                .map(productVariantMapper::toDto)
                .toList();
    }
}
