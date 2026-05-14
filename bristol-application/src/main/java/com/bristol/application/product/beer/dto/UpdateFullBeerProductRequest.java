package com.bristol.application.product.beer.dto;

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
public class UpdateFullBeerProductRequest {

    @NotNull
    @Valid
    private UpdateBeerProductRequest product;

    /** null = keep existing variants; non-null = replace all */
    private List<EmbeddedVariantRequest> variants;

    /** null = keep existing images; non-null = replace all */
    private List<ProductImageRequest> images;
}
