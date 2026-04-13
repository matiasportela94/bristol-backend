package com.bristol.application.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleDeliveryRequest {

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    private String reason;
}
