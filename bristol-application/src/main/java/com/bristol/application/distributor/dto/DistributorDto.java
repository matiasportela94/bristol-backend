package com.bristol.application.distributor.dto;

import com.bristol.domain.distributor.DistributorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorDto {
    private String id;
    private String userId;
    private String userEmail;
    private String address;
    private String phone;
    private String cuit;
    private String razonSocial;
    private String deliveryZoneId;
    private String deliveryZone;
    private DistributorStatus status;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private Integer totalBeers;
    private BigDecimal totalProfit;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UploadedFileDto> uploadedFiles;
}
