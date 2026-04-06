package com.bristol.application.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking delivery as failed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkDeliveryFailedRequest {

    @NotBlank(message = "Driver notes are required when marking as failed")
    private String driverNotes;
}
