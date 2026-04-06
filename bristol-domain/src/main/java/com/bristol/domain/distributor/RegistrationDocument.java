package com.bristol.domain.distributor;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

/**
 * Document attached to a distributor registration request.
 */
@Getter
@Builder(toBuilder = true)
public class RegistrationDocument {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );

    private final RegistrationDocumentId id;
    private final DistributorRegistrationRequestId registrationRequestId;
    private final DistributorId distributorId;
    private final String fileName;
    private final String contentType;
    private final RegistrationDocumentType documentType;
    private final long fileSize;
    @Getter(AccessLevel.NONE)
    private final byte[] fileData;
    private final Instant createdAt;

    public static RegistrationDocument create(
            DistributorRegistrationRequestId registrationRequestId,
            String fileName,
            String contentType,
            RegistrationDocumentType documentType,
            long fileSize,
            byte[] fileData,
            Instant now
    ) {
        validate(registrationRequestId, fileName, contentType, documentType, fileSize, fileData);

        return RegistrationDocument.builder()
                .id(RegistrationDocumentId.generate())
                .registrationRequestId(registrationRequestId)
                .distributorId(null)
                .fileName(fileName.trim())
                .contentType(contentType.trim())
                .documentType(documentType)
                .fileSize(fileSize)
                .fileData(Arrays.copyOf(fileData, fileData.length))
                .createdAt(now)
                .build();
    }

    public static RegistrationDocument createForDistributor(
            DistributorId distributorId,
            String fileName,
            String contentType,
            RegistrationDocumentType documentType,
            long fileSize,
            byte[] fileData,
            Instant now
    ) {
        if (distributorId == null) {
            throw new ValidationException("Distributor ID is required");
        }

        validate(fileName, contentType, documentType, fileSize, fileData);

        return RegistrationDocument.builder()
                .id(RegistrationDocumentId.generate())
                .registrationRequestId(null)
                .distributorId(distributorId)
                .fileName(fileName.trim())
                .contentType(contentType.trim())
                .documentType(documentType)
                .fileSize(fileSize)
                .fileData(Arrays.copyOf(fileData, fileData.length))
                .createdAt(now)
                .build();
    }

    public byte[] getFileData() {
        return fileData != null ? Arrays.copyOf(fileData, fileData.length) : null;
    }

    public RegistrationDocument assignToDistributor(DistributorId distributorId) {
        if (distributorId == null) {
            throw new ValidationException("Distributor ID is required");
        }

        return this.toBuilder()
                .registrationRequestId(null)
                .distributorId(distributorId)
                .build();
    }

    private static void validate(
            DistributorRegistrationRequestId registrationRequestId,
            String fileName,
            String contentType,
            RegistrationDocumentType documentType,
            long fileSize,
            byte[] fileData
    ) {
        if (registrationRequestId == null) {
            throw new ValidationException("Registration request ID is required");
        }
        validate(fileName, contentType, documentType, fileSize, fileData);
    }

    private static void validate(
            String fileName,
            String contentType,
            RegistrationDocumentType documentType,
            long fileSize,
            byte[] fileData
    ) {
        if (fileName == null || fileName.isBlank()) {
            throw new ValidationException("Document file name is required");
        }
        if (fileName.length() > 255) {
            throw new ValidationException("Document file name cannot exceed 255 characters");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new ValidationException("Document content type is required");
        }
        String normalizedContentType = contentType.trim().toLowerCase();
        if (!ALLOWED_CONTENT_TYPES.contains(normalizedContentType)) {
            throw new ValidationException("Document content type is not supported");
        }
        if (documentType == null) {
            throw new ValidationException("Document type is required");
        }
        if (fileData == null || fileData.length == 0) {
            throw new ValidationException("Document data is required");
        }
        if (fileSize <= 0) {
            throw new ValidationException("Document file size must be greater than zero");
        }
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new ValidationException("Document file size cannot exceed 5 MB");
        }
    }
}
