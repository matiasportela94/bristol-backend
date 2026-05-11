package com.bristol.application.catalog.specialtype.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for SpecialType information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialTypeDto {

    private String id;
    private String code;
    private String name;
    private String description;
    private boolean requiresQuote;
    private boolean active;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
