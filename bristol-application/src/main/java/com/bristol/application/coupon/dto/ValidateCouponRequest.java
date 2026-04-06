package com.bristol.application.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for validating a coupon code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Order total is required")
    @Positive(message = "Order total must be positive")
    private BigDecimal orderTotal;

    private String userId;
}
