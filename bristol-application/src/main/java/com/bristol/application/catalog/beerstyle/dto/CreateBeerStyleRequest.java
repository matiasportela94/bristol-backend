package com.bristol.application.catalog.beerstyle.dto;

import com.bristol.domain.catalog.BeerStyleCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create a new beer style.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeerStyleRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private BeerStyleCategory category;

    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = false, message = "ABV must be positive")
    private java.math.BigDecimal abv;

    @jakarta.validation.constraints.Min(value = 0, message = "IBU cannot be negative")
    private Integer ibu;

    @jakarta.validation.constraints.Min(value = 0, message = "SRM cannot be negative")
    private Integer srm;
}
