package com.bristol.application.distributor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDistributorRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "DNI is required")
    private String dni;

    @NotBlank(message = "CUIT is required")
    private String cuit;

    @NotBlank(message = "Razon Social is required")
    private String razonSocial;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Delivery zone ID is required")
    private String deliveryZoneId;
}
