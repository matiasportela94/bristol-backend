package com.bristol.application.catalog.beerstyle.dto;

import com.bristol.domain.catalog.BeerStyleCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for BeerStyle information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerStyleDto {

    private String id;
    private String code;
    private String name;
    private String description;
    private BeerStyleCategory category;
    private boolean active;
    private Integer displayOrder;
    private boolean hasImage;
    private java.math.BigDecimal abv;
    private Integer ibu;
    private Integer srm;
    private Instant createdAt;
    private Instant updatedAt;
}
