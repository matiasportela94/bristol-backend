package com.bristol.application.distributor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * Request DTO to reject a distributor registration.
 */
@Value
public class RejectDistributorRegistrationRequest {

    @NotBlank(message = "Rejection reason is required")
    String reason;
}
