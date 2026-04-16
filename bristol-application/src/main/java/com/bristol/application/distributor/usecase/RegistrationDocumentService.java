package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.RegistrationDocumentPayload;
import com.bristol.application.distributor.dto.UploadedFileDto;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.distributor.RegistrationDocument;
import com.bristol.domain.distributor.RegistrationDocumentId;
import com.bristol.domain.distributor.RegistrationDocumentRepository;
import com.bristol.domain.distributor.RegistrationDocumentType;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Coordinates validation and persistence for distributor registration documents.
 */
@Component
@RequiredArgsConstructor
public class RegistrationDocumentService {

    private static final int MAX_DOCUMENT_COUNT = 5;

    private final RegistrationDocumentRepository registrationDocumentRepository;
    private final DistributorRegistrationRepository distributorRegistrationRepository;
    private final TimeProvider timeProvider;

    public List<RegistrationDocument> createDocuments(
            DistributorRegistrationRequestId registrationRequestId,
            List<RegistrationDocumentPayload> documents
    ) {
        if (documents == null || documents.isEmpty()) {
            throw new ValidationException("At least one registration document is required");
        }

        if (documents.size() > MAX_DOCUMENT_COUNT) {
            throw new ValidationException("Registration cannot include more than 5 documents");
        }

        Instant now = timeProvider.now();

        return documents.stream()
                .map(document -> RegistrationDocument.create(
                        registrationRequestId,
                        document.getFileName(),
                        document.getContentType(),
                        RegistrationDocumentType.ARCA_REGISTRATION,
                        document.getFileSize(),
                        document.getFileData(),
                        now
                ))
                .map(registrationDocumentRepository::save)
                .toList();
    }

    public List<RegistrationDocument> getDocuments(DistributorRegistrationRequestId registrationRequestId) {
        return registrationDocumentRepository.findByRegistrationRequestId(registrationRequestId);
    }

    public List<RegistrationDocument> getDocuments(DistributorId distributorId) {
        return registrationDocumentRepository.findByDistributorId(distributorId);
    }

    public RegistrationDocument createDocumentForDistributor(
            DistributorId distributorId,
            String fileName,
            String contentType,
            String documentType,
            long fileSize,
            byte[] fileData
    ) {
        long existingDocumentCount = getDocuments(distributorId).size();
        if (existingDocumentCount >= MAX_DOCUMENT_COUNT) {
            throw new ValidationException("Distributor cannot include more than 5 documents");
        }

        RegistrationDocument document = RegistrationDocument.createForDistributor(
                distributorId,
                fileName,
                contentType,
                resolveDocumentType(documentType),
                fileSize,
                fileData,
                timeProvider.now()
        );
        return registrationDocumentRepository.save(document);
    }

    public RegistrationDocument getDocument(RegistrationDocumentId documentId) {
        return registrationDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ValidationException("Registration document not found: " + documentId.asString()));
    }

    public List<UploadedFileDto> toDtos(DistributorRegistrationRequestId registrationRequestId) {
        return toDtos(registrationRequestId, getDocuments(registrationRequestId));
    }

    public List<UploadedFileDto> toDtos(
            DistributorRegistrationRequestId registrationRequestId,
            List<RegistrationDocument> documents
    ) {
        return documents.stream()
                .map(document -> UploadedFileDto.builder()
                        .id(document.getId().asString())
                        .distributorId(registrationRequestId.asString())
                        .fileName(document.getFileName())
                        .fileUrl("/api/distributors/registrations/" + registrationRequestId.asString()
                                + "/documents/" + document.getId().asString() + "/download")
                        .fileType(resolveFileType(document.getContentType()))
                        .documentType(document.getDocumentType().name().toLowerCase())
                        .fileSize(document.getFileSize())
                        .uploadedAt(document.getCreatedAt().toString())
                        .description(null)
                        .build())
                .toList();
    }

    public List<UploadedFileDto> toDtos(DistributorId distributorId) {
        return getDocuments(distributorId).stream()
                .map(document -> UploadedFileDto.builder()
                        .id(document.getId().asString())
                        .distributorId(distributorId.asString())
                        .fileName(document.getFileName())
                        .fileUrl("/api/distributors/" + distributorId.asString()
                                + "/documents/" + document.getId().asString() + "/download")
                        .fileType(resolveFileType(document.getContentType()))
                        .documentType(document.getDocumentType().name().toLowerCase())
                        .fileSize(document.getFileSize())
                        .uploadedAt(document.getCreatedAt().toString())
                        .description(null)
                        .build())
                .toList();
    }

    public void assignDocumentsToDistributor(
            DistributorRegistrationRequestId registrationRequestId,
            DistributorId distributorId
    ) {
        getDocuments(registrationRequestId).stream()
                .map(document -> document.assignToDistributor(distributorId))
                .forEach(registrationDocumentRepository::save);
    }

    public RegistrationDocument getDocumentForRegistration(
            DistributorRegistrationRequestId registrationRequestId,
            RegistrationDocumentId documentId
    ) {
        distributorRegistrationRepository.findById(registrationRequestId)
                .orElseThrow(() -> new ValidationException("Registration request not found: " + registrationRequestId.asString()));

        RegistrationDocument document = getDocument(documentId);
        if (document.getRegistrationRequestId() == null || !document.getRegistrationRequestId().equals(registrationRequestId)) {
            throw new ValidationException("Document does not belong to the specified registration request");
        }
        return document;
    }

    public RegistrationDocument getDocumentForDistributor(
            DistributorId distributorId,
            RegistrationDocumentId documentId
    ) {
        RegistrationDocument document = getDocument(documentId);
        if (document.getDistributorId() == null || !document.getDistributorId().equals(distributorId)) {
            throw new ValidationException("Document does not belong to the specified distributor");
        }
        return document;
    }

    public void deleteDocumentForDistributor(
            DistributorId distributorId,
            RegistrationDocumentId documentId
    ) {
        getDocumentForDistributor(distributorId, documentId);
        registrationDocumentRepository.delete(documentId);
    }

    private String resolveFileType(String contentType) {
        return "application/pdf".equalsIgnoreCase(contentType) ? "pdf" : "image";
    }

    private RegistrationDocumentType resolveDocumentType(String documentType) {
        if (documentType == null || documentType.isBlank()) {
            return RegistrationDocumentType.OTHER;
        }

        return switch (documentType.trim().toLowerCase()) {
            case "arca_registration" -> RegistrationDocumentType.ARCA_REGISTRATION;
            case "cuit" -> RegistrationDocumentType.CUIT;
            case "contract" -> RegistrationDocumentType.CONTRACT;
            default -> RegistrationDocumentType.OTHER;
        };
    }
}
