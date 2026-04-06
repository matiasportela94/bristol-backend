package com.bristol.application.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDto {
    private String id;
    private String userId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String postalCode;
    private String deliveryZoneId;
    private boolean isDefault;
    private Instant createdAt;
    private Instant updatedAt;
}
