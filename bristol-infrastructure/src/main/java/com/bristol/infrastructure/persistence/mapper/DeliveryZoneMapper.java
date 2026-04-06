package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.delivery.DeliveryZone;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.infrastructure.persistence.entity.DeliveryZoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * MapStruct mapper for DeliveryZone domain object and DeliveryZoneEntity.
 */
@Mapper(componentModel = "spring")
public interface DeliveryZoneMapper {

    @Mapping(target = "id", expression = "java(toDeliveryZoneId(entity.getId()))")
    @Mapping(target = "isActive", expression = "java(Boolean.TRUE.equals(entity.getIsActive()))")
    DeliveryZone toDomain(DeliveryZoneEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "isActive", expression = "java(domain.isActive())")
    DeliveryZoneEntity toEntity(DeliveryZone domain);

    default DeliveryZoneId toDeliveryZoneId(UUID uuid) {
        return uuid != null ? new DeliveryZoneId(uuid) : null;
    }

    default UUID toUUID(DeliveryZoneId id) {
        return id != null ? id.getValue() : null;
    }
}
