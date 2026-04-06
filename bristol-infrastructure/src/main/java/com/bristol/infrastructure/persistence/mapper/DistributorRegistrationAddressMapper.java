package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.DistributorRegistrationAddress;
import com.bristol.domain.distributor.DistributorRegistrationAddressId;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.infrastructure.persistence.entity.DistributorRegistrationAddressEntity;
import org.springframework.stereotype.Component;

@Component
public class DistributorRegistrationAddressMapper {

    public DistributorRegistrationAddress toDomain(DistributorRegistrationAddressEntity entity) {
        return DistributorRegistrationAddress.builder()
                .id(new DistributorRegistrationAddressId(entity.getId()))
                .registrationRequestId(new DistributorRegistrationRequestId(entity.getRegistrationRequestId()))
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .province(entity.getProvince())
                .postalCode(entity.getPostalCode())
                .deliveryZoneId(new DeliveryZoneId(entity.getDeliveryZoneId()))
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public DistributorRegistrationAddressEntity toEntity(DistributorRegistrationAddress domain) {
        return DistributorRegistrationAddressEntity.builder()
                .id(domain.getId().getValue())
                .registrationRequestId(domain.getRegistrationRequestId().getValue())
                .addressLine1(domain.getAddressLine1())
                .addressLine2(domain.getAddressLine2())
                .city(domain.getCity())
                .province(domain.getProvince())
                .postalCode(domain.getPostalCode())
                .deliveryZoneId(domain.getDeliveryZoneId().getValue())
                .isDefault(domain.isDefault())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
