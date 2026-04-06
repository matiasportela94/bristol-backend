package com.bristol.application.distributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationShippingAddressDto {
    private String id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String postalCode;
    private String deliveryZoneId;
    private String deliveryZone;
    private boolean isDefault;
}
