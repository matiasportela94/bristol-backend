package com.bristol.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned for a product image stored in the database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {

    private String id;
    private String fileName;
    private String contentType;
    private String dataBase64;
    private Integer displayOrder;
    private boolean primary;
}
