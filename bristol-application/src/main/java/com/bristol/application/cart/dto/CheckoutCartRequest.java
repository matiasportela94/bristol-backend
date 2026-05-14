package com.bristol.application.cart.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutCartRequest {

    /** ID de dirección de usuario (legacy). Mutuamente excluyente con branchId. */
    private String shippingAddressId;

    /** ID de sucursal del distribuidor. Alternativa a shippingAddressId. */
    private String branchId;

    private String notes;

    private String couponCode;

    @AssertTrue(message = "Debe especificarse shippingAddressId o branchId")
    public boolean hasShippingTarget() {
        return (shippingAddressId != null && !shippingAddressId.isBlank())
                || (branchId != null && !branchId.isBlank());
    }
}
