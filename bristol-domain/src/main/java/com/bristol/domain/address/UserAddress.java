package com.bristol.domain.address;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * UserAddress domain entity.
 * Represents a shipping address for a user.
 */
@Getter
@Builder(toBuilder = true)
public class UserAddress {

    private final UserAddressId id;
    private final UserId userId;
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String province;
    private final String postalCode;
    private final DeliveryZoneId deliveryZoneId;
    private final boolean isDefault;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new user address.
     */
    public static UserAddress create(
            UserId userId,
            String addressLine1,
            String addressLine2,
            String city,
            String province,
            String postalCode,
            DeliveryZoneId deliveryZoneId,
            boolean isDefault,
            Instant now
    ) {
        validateAddress(addressLine1, city, province);

        return UserAddress.builder()
                .id(UserAddressId.generate())
                .userId(userId)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .province(province)
                .postalCode(postalCode)
                .deliveryZoneId(deliveryZoneId)
                .isDefault(isDefault)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Set this address as default.
     */
    public UserAddress setAsDefault(Instant now) {
        return this.toBuilder()
                .isDefault(true)
                .updatedAt(now)
                .build();
    }

    /**
     * Unset this address as default.
     */
    public UserAddress unsetAsDefault(Instant now) {
        return this.toBuilder()
                .isDefault(false)
                .updatedAt(now)
                .build();
    }

    /**
     * Update address information.
     */
    public UserAddress update(
            String addressLine1,
            String addressLine2,
            String city,
            String province,
            String postalCode,
            DeliveryZoneId deliveryZoneId,
            Instant now
    ) {
        validateAddress(addressLine1, city, province);

        return this.toBuilder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .province(province)
                .postalCode(postalCode)
                .deliveryZoneId(deliveryZoneId)
                .updatedAt(now)
                .build();
    }

    /**
     * Get full address as single string.
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

    private static void validateAddress(String addressLine1, String city, String province) {
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
