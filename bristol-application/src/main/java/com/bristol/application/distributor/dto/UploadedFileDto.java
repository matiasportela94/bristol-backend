package com.bristol.application.distributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for distributor or registration supporting documents.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileDto {
    private String id;
    private String distributorId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String documentType;
    private Long fileSize;
    private String uploadedAt;
    private String description;
}
