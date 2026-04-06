package com.bristol.application.distributor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating distributor information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDistributorRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "CUIT is required")
    private String cuit;

    @NotBlank(message = "Razon Social is required")
    private String razonSocial;

    @NotBlank(message = "Delivery zone ID is required")
    private String deliveryZoneId;
}
