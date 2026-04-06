package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.DistributorRegistrationAddress;
import com.bristol.domain.distributor.DistributorRegistrationAddressId;
import com.bristol.domain.distributor.DistributorRegistrationAddressRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.infrastructure.persistence.entity.DistributorRegistrationAddressEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DistributorRegistrationAddressRepositoryImpl implements DistributorRegistrationAddressRepository {

    private final JpaDistributorRegistrationAddressRepository jpaRepository;

    @Override
    public DistributorRegistrationAddress save(DistributorRegistrationAddress address) {
        DistributorRegistrationAddressEntity savedEntity = jpaRepository.save(toEntity(address));
        return toDomain(savedEntity);
    }

    @Override
    public List<DistributorRegistrationAddress> findByRegistrationRequestId(DistributorRegistrationRequestId registrationRequestId) {
        return jpaRepository.findByRegistrationRequestIdOrderByCreatedAtAsc(registrationRequestId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private DistributorRegistrationAddress toDomain(DistributorRegistrationAddressEntity entity) {
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

    private DistributorRegistrationAddressEntity toEntity(DistributorRegistrationAddress address) {
        return DistributorRegistrationAddressEntity.builder()
                .id(address.getId().getValue())
                .registrationRequestId(address.getRegistrationRequestId().getValue())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryZoneId(address.getDeliveryZoneId().getValue())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
