package com.bristol.application.deliveryzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating delivery zone.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryZoneRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private String description;
}
