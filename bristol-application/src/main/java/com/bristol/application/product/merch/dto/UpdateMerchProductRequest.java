package com.bristol.application.product.merch.dto;

import com.bristol.domain.catalog.MerchCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to update a merch product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMerchProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be positive")
    private BigDecimal basePrice;

    @NotBlank(message = "Merch type ID is required")
    private String merchTypeId;

    @NotNull(message = "Merch category is required")
    private MerchCategory merchCategory;

    private String material;

    private String brand;
}
