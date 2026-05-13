package com.bristol.application.distributorbranch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDistributorBranchRequest {

    @NotBlank
    private String name;

    private String address;
    private String city;
    private String province;
    private String codigoPostal;

    // Optional — auto-detected from address via Google Geocoding API.
    // Override only if automatic detection fails or needs correction.
    @Pattern(regexp = "^(sur|norte|centro)$", flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Delivery zone must be one of: sur, norte, centro")
    private String deliveryZone;
}
