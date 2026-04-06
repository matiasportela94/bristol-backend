package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.delivery.DeliveryCalendar;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.infrastructure.persistence.entity.DeliveryCalendarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * MapStruct mapper for DeliveryCalendar domain object and DeliveryCalendarEntity.
 */
@Mapper(componentModel = "spring")
public interface DeliveryCalendarMapper {

    @Mapping(target = "id", expression = "java(toDeliveryCalendarId(entity.getId()))")
    @Mapping(target = "deliveryZoneId", expression = "java(toDeliveryZoneId(entity.getDeliveryZoneId()))")
    DeliveryCalendar toDomain(DeliveryCalendarEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "deliveryZoneId", expression = "java(toUUID(domain.getDeliveryZoneId()))")
    DeliveryCalendarEntity toEntity(DeliveryCalendar domain);

    default DeliveryCalendarId toDeliveryCalendarId(UUID uuid) {
        return uuid != null ? new DeliveryCalendarId(uuid) : null;
    }

    default DeliveryZoneId toDeliveryZoneId(UUID uuid) {
        return uuid != null ? new DeliveryZoneId(uuid) : null;
    }

    default UUID toUUID(DeliveryCalendarId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DeliveryZoneId id) {
        return id != null ? id.getValue() : null;
    }
}
