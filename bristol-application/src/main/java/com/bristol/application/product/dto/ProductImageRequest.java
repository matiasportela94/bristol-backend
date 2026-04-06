package com.bristol.application.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a product image sent by clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {

    private String fileName;

    @NotBlank(message = "Image content type is required")
    private String contentType;

    @NotBlank(message = "Image data is required")
    private String dataBase64;

    @Min(value = 0, message = "Image display order cannot be negative")
    private Integer displayOrder;

    private Boolean primary;
}
