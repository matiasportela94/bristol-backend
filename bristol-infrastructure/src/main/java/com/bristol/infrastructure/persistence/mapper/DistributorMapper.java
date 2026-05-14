package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorStatus;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.DistributorEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mapper for Distributor domain object and DistributorEntity.
 *
 */
@Component("distributorPersistenceMapper")
public class DistributorMapper {

    public Distributor toDomain(DistributorEntity entity) {
        if (entity == null) {
            return null;
        }

        return Distributor.builder()
                .id(toDistributorId(entity.getId()))
                .userId(toUserId(entity.getUserId()))
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .dni(null)
                .cuit(entity.getCuit())
                .razonSocial(entity.getBusinessName())
                .dateOfBirth(null)
                .status(toDomainStatus(entity.getStatus()))
                .totalOrders(0)
                .totalSpent(entity.getTotalSpent() != null ? entity.getTotalSpent() : BigDecimal.ZERO)
                .totalBeers(entity.getTotalBeersPurchased() != null ? entity.getTotalBeersPurchased() : 0)
                .totalProfit(BigDecimal.ZERO)
                .lastOrderAt(null)
                .lastSignInAt(null)
                .emailConfirmedAt(null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DistributorEntity toEntity(Distributor domain) {
        if (domain == null) {
            return null;
        }

        return DistributorEntity.builder()
                .id(toUUID(domain.getId()))
                .userId(toUUID(domain.getUserId()))
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .cuit(domain.getCuit())
                .businessName(domain.getRazonSocial())
                .status(toEntityStatus(domain.getStatus()))
                .totalSpent(domain.getTotalSpent() != null ? domain.getTotalSpent() : BigDecimal.ZERO)
                .totalBeersPurchased(domain.getTotalBeers() != null ? domain.getTotalBeers() : 0)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private DistributorId toDistributorId(UUID uuid) {
        return uuid != null ? new DistributorId(uuid) : null;
    }

    private UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    private UUID toUUID(DistributorId id) {
        return id != null ? id.getValue() : null;
    }

    private UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    private DistributorStatus toDomainStatus(DistributorEntity.DistributorStatusEnum status) {
        return status != null ? DistributorStatus.valueOf(status.name()) : null;
    }

    private DistributorEntity.DistributorStatusEnum toEntityStatus(DistributorStatus status) {
        return status != null ? DistributorEntity.DistributorStatusEnum.valueOf(status.name()) : null;
    }
}
