package com.bristol.domain.order;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

/**
 * ShippingAddress value object.
 * Represents a shipping address for an order.
 */
@Getter
@Builder
public class ShippingAddress {

    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String province;
    private final String postalCode;
    private final DeliveryZoneId deliveryZoneId;

    /**
     * Create shipping address.
     */
    public static ShippingAddress of(
            String addressLine1,
            String addressLine2,
            String city,
            String province,
            String postalCode,
            DeliveryZoneId deliveryZoneId
    ) {
        validate(addressLine1, city, province);

        return ShippingAddress.builder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .province(province)
                .postalCode(postalCode)
                .deliveryZoneId(deliveryZoneId)
                .build();
    }

    /**
     * Get full address as string.
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(addressLine1);
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city);
        sb.append(", ").append(province);
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        return sb.toString();
    }

    private static void validate(String addressLine1, String city, String province) {
        if (addressLine1 == null || addressLine1.trim().isEmpty()) {
            throw new ValidationException("Address line 1 is required");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("City is required");
        }
        if (province == null || province.trim().isEmpty()) {
            throw new ValidationException("Province is required");
        }
    }
}
