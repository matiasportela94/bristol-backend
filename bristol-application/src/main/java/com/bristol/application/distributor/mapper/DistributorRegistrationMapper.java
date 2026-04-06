package com.bristol.application.distributor.mapper;

import com.bristol.application.distributor.dto.DistributorRegistrationDto;
import com.bristol.application.distributor.dto.RegistrationShippingAddressDto;
import com.bristol.application.distributor.dto.UploadedFileDto;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Mapper for DistributorRegistrationRequest entity.
 */
@Component
public class DistributorRegistrationMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public DistributorRegistrationDto toDto(DistributorRegistrationRequest entity) {
        return toDto(entity, List.of(), List.of());
    }

    public DistributorRegistrationDto toDto(DistributorRegistrationRequest entity, List<UploadedFileDto> uploadedFiles) {
        return toDto(entity, List.of(), uploadedFiles);
    }

    public DistributorRegistrationDto toDto(
            DistributorRegistrationRequest entity,
            List<RegistrationShippingAddressDto> shippingAddresses,
            List<UploadedFileDto> uploadedFiles
    ) {
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
                .deliveryZone(resolveDefaultDeliveryZone(shippingAddresses))
                .status(entity.getStatus().name())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(FORMATTER.format(entity.getCreatedAt()))
                .updatedAt(FORMATTER.format(entity.getUpdatedAt()))
                .shippingAddresses(shippingAddresses)
                .uploadedFiles(uploadedFiles)
                .build();
    }

    private String resolveDefaultDeliveryZone(List<RegistrationShippingAddressDto> shippingAddresses) {
        return shippingAddresses.stream()
                .filter(RegistrationShippingAddressDto::isDefault)
                .findFirst()
                .or(() -> shippingAddresses.stream().findFirst())
                .map(RegistrationShippingAddressDto::getDeliveryZone)
                .orElse(null);
    }
}
