package com.bristol.application.distributor.mapper;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.dto.UploadedFileDto;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DistributorRegistrationMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public DistributorRegistrationDto toDto(DistributorRegistrationRequest entity) {
        return toDto(entity, List.of());
    }

    public DistributorRegistrationDto toDto(
            DistributorRegistrationRequest entity,
            List<?> ignoredAddresses,
            List<UploadedFileDto> uploadedFiles
    ) {
        return toDto(entity, uploadedFiles);
    }

    public DistributorRegistrationDto toDto(DistributorRegistrationRequest entity, List<UploadedFileDto> uploadedFiles) {
        return DistributorRegistrationDto.builder()
                .id(entity.getId().getValue().toString())
                .razonSocial(entity.getRazonSocial())
                .cuit(entity.getCuit())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .provincia(entity.getProvincia())
                .ciudad(entity.getCiudad())
                .direccion(entity.getDireccion())
                .codigoPostal(entity.getCodigoPostal())
                .status(entity.getStatus().name())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(FORMATTER.format(entity.getCreatedAt()))
                .updatedAt(FORMATTER.format(entity.getUpdatedAt()))
                .uploadedFiles(uploadedFiles)
                .build();
    }
}
