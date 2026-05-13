package com.bristol.application.distributorbranch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorBranchDto {

    private String id;
    private String distributorId;
    private String name;
    private String address;
    private String city;
    private String province;
    private String codigoPostal;
    private String deliveryZoneId;
    private String deliveryZone;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
