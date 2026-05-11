package com.bristol.application.catalog.merchtype.dto;

import com.bristol.domain.catalog.MerchCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create a new merch type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchTypeRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private MerchCategory category;
}
