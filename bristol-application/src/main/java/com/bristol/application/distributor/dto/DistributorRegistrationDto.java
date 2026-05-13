package com.bristol.application.distributor.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * DTO for distributor registration request.
 */
@Value
@Builder
public class DistributorRegistrationDto {
    String id;
    String razonSocial;
    String cuit;
    String email;
    String telefono;
    String provincia;
    String ciudad;
    String direccion;
    String codigoPostal;
    String status;
    String rejectionReason;
    String createdAt;
    String updatedAt;
    List<UploadedFileDto> uploadedFiles;
}
