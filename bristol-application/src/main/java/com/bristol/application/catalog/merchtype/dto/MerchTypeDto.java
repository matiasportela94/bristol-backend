package com.bristol.application.catalog.merchtype.dto;

import com.bristol.domain.catalog.MerchCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for MerchType information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchTypeDto {

    private String id;
    private String code;
    private String name;
    private String description;
    private MerchCategory category;
    private boolean active;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
