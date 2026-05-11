package com.bristol.application.catalog.specialtype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to update a special type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpecialTypeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private String description;

    private boolean requiresQuote;

    private Integer displayOrder;
}
