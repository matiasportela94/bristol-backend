package com.bristol.application.product.merch.dto;

import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.application.productvariant.dto.EmbeddedVariantRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFullMerchProductRequest {

    @NotNull
    @Valid
    private CreateMerchProductRequest product;

    private List<EmbeddedVariantRequest> variants;

    private List<ProductImageRequest> images;
}
