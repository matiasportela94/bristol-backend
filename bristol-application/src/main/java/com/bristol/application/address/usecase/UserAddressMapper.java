package com.bristol.application.address.usecase;

import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.domain.address.UserAddress;
import org.springframework.stereotype.Component;

@Component
public class UserAddressMapper {
    public UserAddressDto toDto(UserAddress address) {
        return UserAddressDto.builder()
                .id(address.getId().getValue().toString())
                .userId(address.getUserId().getValue().toString())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryZoneId(address.getDeliveryZoneId().getValue().toString())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
