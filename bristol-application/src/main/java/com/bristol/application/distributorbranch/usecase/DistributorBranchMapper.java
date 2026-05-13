package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.distributor.DistributorBranch;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DistributorBranchMapper {

    public DistributorBranchDto toDto(DistributorBranch branch) {
        return DistributorBranchDto.builder()
                .id(branch.getId().getValue().toString())
                .distributorId(branch.getDistributorId().getValue().toString())
                .name(branch.getName())
                .address(branch.getAddress())
                .city(branch.getCity())
                .province(branch.getProvince())
                .codigoPostal(branch.getCodigoPostal())
                .deliveryZoneId(branch.getDeliveryZoneId() != null ? branch.getDeliveryZoneId().getValue().toString() : null)
                .deliveryZone(branch.getDeliveryZoneId() != null ? resolveZoneName(branch) : null)
                .active(branch.isActive())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

    private String resolveZoneName(DistributorBranch branch) {
        return Arrays.stream(DeliveryZoneType.values())
                .filter(z -> z.getUuid().equals(branch.getDeliveryZoneId().getValue()))
                .findFirst()
                .map(z -> z.name().toLowerCase())
                .orElse(null);
    }
}
