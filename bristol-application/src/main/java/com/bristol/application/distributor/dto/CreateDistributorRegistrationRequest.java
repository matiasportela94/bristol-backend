package com.bristol.application.distributor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO to register a new distributor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDistributorRegistrationRequest {

    @NotBlank(message = "Razón social is required")
    String razonSocial;

    @NotBlank(message = "CUIT is required")
    @Pattern(regexp = "^\\d{2}-?\\d{8}-?\\d{1}$",
             message = "DNI/CUIT must follow format: XX-XXXXXXXX-X")
    String cuit;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;

    @NotBlank(message = "Telefono is required")
    @Pattern(regexp = "^[0-9+\\s\\-()]+$",
             message = "Telefono must contain only numbers, spaces, and +-()")
    String telefono;

    // Dirección fiscal
    String provincia;

    String ciudad;

    @NotBlank(message = "Direccion is required")
    String direccion;

    String codigoPostal;

    List<RegistrationDocumentPayload> documents;
}
