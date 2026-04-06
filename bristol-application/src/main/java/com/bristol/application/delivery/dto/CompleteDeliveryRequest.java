package com.bristol.application.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for completing a delivery.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteDeliveryRequest {

    @NotNull(message = "Actual delivery date is required")
    private LocalDate actualDeliveryDate;

    private String driverNotes;
}
