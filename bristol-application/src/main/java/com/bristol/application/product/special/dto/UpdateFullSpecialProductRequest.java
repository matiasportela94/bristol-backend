package com.bristol.application.product.special.dto;

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
public class UpdateFullSpecialProductRequest {

    @NotNull
    @Valid
    private UpdateSpecialProductRequest product;

    /** null = keep existing variants; non-null = replace all */
    private List<EmbeddedVariantRequest> variants;

    /** null = keep existing images; non-null = replace all */
    private List<ProductImageRequest> images;
}
