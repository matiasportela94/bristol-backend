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
public class BreweryBatchDto {
    private String id;
    private String beerStyleId;
    private String beerStyleName;
    private String beerStyleCode;
    private int cansProduced;
    private Integer canCapacityMl;
    private String notes;
    private Instant createdAt;
}
