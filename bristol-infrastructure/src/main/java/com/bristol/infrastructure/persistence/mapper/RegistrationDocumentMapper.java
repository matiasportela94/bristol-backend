package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.RegistrationDocument;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.infrastructure.persistence.entity.RegistrationDocumentEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Maps registration document domain objects to JPA entities.
 */
@Component
public class RegistrationDocumentMapper {

    public RegistrationDocument toDomain(RegistrationDocumentEntity entity) {
        return RegistrationDocument.builder()
                .id(new RegistrationDocumentId(entity.getId()))
                .registrationRequestId(entity.getRegistrationRequestId() != null
                        ? new DistributorRegistrationRequestId(entity.getRegistrationRequestId())
                        : null)
                .distributorId(entity.getDistributorId() != null
                        ? new DistributorId(entity.getDistributorId())
                        : null)
                .fileName(entity.getFileName())
                .contentType(entity.getContentType())
                .documentType(entity.getDocumentType())
                .fileSize(entity.getFileSize())
                .fileData(entity.getFileData() != null ? Arrays.copyOf(entity.getFileData(), entity.getFileData().length) : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RegistrationDocumentEntity toEntity(RegistrationDocument domain) {
        return RegistrationDocumentEntity.builder()
                .id(domain.getId().getValue())
                .registrationRequestId(domain.getRegistrationRequestId() != null ? domain.getRegistrationRequestId().getValue() : null)
                .distributorId(domain.getDistributorId() != null ? domain.getDistributorId().getValue() : null)
                .fileName(domain.getFileName())
                .contentType(domain.getContentType())
                .documentType(domain.getDocumentType())
                .fileSize(domain.getFileSize())
                .fileData(domain.getFileData())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
