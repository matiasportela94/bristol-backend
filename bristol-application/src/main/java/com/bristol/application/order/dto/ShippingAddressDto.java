package com.bristol.application.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for shipping address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDto {

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    private String postalCode;

    @NotBlank(message = "Delivery zone ID is required")
    private String deliveryZoneId;
}
