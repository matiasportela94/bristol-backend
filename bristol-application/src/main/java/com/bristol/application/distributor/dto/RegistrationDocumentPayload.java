package com.bristol.application.distributor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw payload for a document attached to a distributor registration request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDocumentPayload {

    private String fileName;
    private String contentType;
    private long fileSize;
    private byte[] fileData;
}
