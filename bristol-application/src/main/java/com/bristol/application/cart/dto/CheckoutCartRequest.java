package com.bristol.application.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartRequest {

    @NotBlank(message = "Shipping address ID is required")
    private String shippingAddressId;

    private String notes;
}
