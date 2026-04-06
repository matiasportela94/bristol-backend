package com.bristol.application.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for starting delivery transit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartTransitRequest {

    private String driverNotes;
}
