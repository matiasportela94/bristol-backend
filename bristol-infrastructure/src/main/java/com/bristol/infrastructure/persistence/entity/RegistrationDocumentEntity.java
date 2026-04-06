package com.bristol.infrastructure.persistence.entity;

import com.bristol.domain.distributor.RegistrationDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * JPA entity for registration_documents table.
 */
@Entity
@Table(name = "registration_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationDocumentEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "registration_request_id", columnDefinition = "UUID")
    private UUID registrationRequestId;

    @Column(name = "distributor_id", columnDefinition = "UUID")
    private UUID distributorId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private RegistrationDocumentType documentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "file_data", nullable = false, columnDefinition = "BYTEA")
    private byte[] fileData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = createdAt != null ? createdAt : Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (fileData != null) {
            fileData = Arrays.copyOf(fileData, fileData.length);
        }
    }
}
