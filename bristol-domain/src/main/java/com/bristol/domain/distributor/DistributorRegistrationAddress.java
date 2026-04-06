package com.bristol.domain.distributor;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class DistributorRegistrationAddress {

    private final DistributorRegistrationAddressId id;
    private final DistributorRegistrationRequestId registrationRequestId;
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String province;
    private final String postalCode;
    private final DeliveryZoneId deliveryZoneId;
    private final boolean isDefault;
    private final Instant createdAt;

    public static DistributorRegistrationAddress create(
            DistributorRegistrationRequestId registrationRequestId,
            String addressLine1,
            String addressLine2,
            String city,
            String province,
            String postalCode,
            DeliveryZoneId deliveryZoneId,
            boolean isDefault,
            Instant now
    ) {
        validate(registrationRequestId, addressLine1, city, province, deliveryZoneId);

        return DistributorRegistrationAddress.builder()
                .id(DistributorRegistrationAddressId.generate())
                .registrationRequestId(registrationRequestId)
                .addressLine1(addressLine1.trim())
                .addressLine2(addressLine2)
                .city(city.trim())
                .province(province.trim())
                .postalCode(postalCode)
                .deliveryZoneId(deliveryZoneId)
                .isDefault(isDefault)
                .createdAt(now)
                .build();
    }

    private static void validate(
            DistributorRegistrationRequestId registrationRequestId,
            String addressLine1,
            String city,
            String province,
            DeliveryZoneId deliveryZoneId
    ) {
        if (registrationRequestId == null) {
            throw new ValidationException("Registration request ID is required");
        }
        if (addressLine1 == null || addressLine1.isBlank()) {
            throw new ValidationException("Shipping address is required");
        }
        if (city == null || city.isBlank()) {
            throw new ValidationException("Shipping city is required");
        }
        if (province == null || province.isBlank()) {
            throw new ValidationException("Shipping province is required");
        }
        if (deliveryZoneId == null) {
            throw new ValidationException("Shipping delivery zone is required");
        }
    }
}
