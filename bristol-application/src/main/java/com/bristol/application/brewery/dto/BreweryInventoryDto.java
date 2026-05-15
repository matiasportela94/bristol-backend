package com.bristol.application.brewery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreweryInventoryDto {
    private String id;
    private String beerStyleId;
    private String beerStyleName;
    private String beerStyleCode;
    private int totalCans;
    private boolean beerStyleHasImage;
    private Instant updatedAt;
}
