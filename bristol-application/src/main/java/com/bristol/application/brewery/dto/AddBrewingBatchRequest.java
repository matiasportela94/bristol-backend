package com.bristol.application.brewery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBrewingBatchRequest {

    @NotBlank(message = "Beer style ID is required")
    private String beerStyleId;

    @NotNull(message = "Cans produced is required")
    @Min(value = 1, message = "Cans produced must be at least 1")
    private Integer cansProduced;

    @Min(value = 1, message = "Can capacity must be positive")
    private Integer canCapacityMl;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost per can cannot be negative")
    private BigDecimal costPerCan;

    private String notes;
}
