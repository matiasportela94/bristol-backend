package com.bristol.application.product.special.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to update a special product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpecialProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    private String description;

    private BigDecimal basePrice;

    @NotBlank(message = "Special type ID is required")
    private String specialTypeId;

    private String notes;

    @NotNull(message = "Requires quote flag is required")
    private Boolean requiresQuote;
}
