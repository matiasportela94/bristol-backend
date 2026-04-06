package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryId;
import com.bristol.domain.delivery.DeliveryStatus;
import com.bristol.domain.order.OrderId;
import com.bristol.infrastructure.persistence.entity.DeliveryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * MapStruct mapper for Delivery domain object and DeliveryEntity.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "id", expression = "java(toDeliveryId(entity.getId()))")
    @Mapping(target = "orderId", expression = "java(toOrderId(entity.getOrderId()))")
    @Mapping(target = "deliveryCalendarId", expression = "java(toDeliveryCalendarId(entity.getDeliveryCalendarId()))")
    @Mapping(target = "status", expression = "java(toDomainStatus(entity.getDeliveryStatus()))")
    Delivery toDomain(DeliveryEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "orderId", expression = "java(toUUID(domain.getOrderId()))")
    @Mapping(target = "deliveryCalendarId", expression = "java(toUUID(domain.getDeliveryCalendarId()))")
    @Mapping(target = "deliveryStatus", expression = "java(toEntityStatus(domain.getStatus()))")
    DeliveryEntity toEntity(Delivery domain);

    // ID conversions
    default DeliveryId toDeliveryId(UUID uuid) {
        return uuid != null ? new DeliveryId(uuid) : null;
    }

    default OrderId toOrderId(UUID uuid) {
        return uuid != null ? new OrderId(uuid) : null;
    }

    default DeliveryCalendarId toDeliveryCalendarId(UUID uuid) {
        return uuid != null ? new DeliveryCalendarId(uuid) : null;
    }

    default UUID toUUID(DeliveryId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(OrderId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DeliveryCalendarId id) {
        return id != null ? id.getValue() : null;
    }

    // Status conversions
    default DeliveryStatus toDomainStatus(DeliveryEntity.DeliveryStatusEnum status) {
        return status != null ? DeliveryStatus.valueOf(status.name()) : null;
    }

    default DeliveryEntity.DeliveryStatusEnum toEntityStatus(DeliveryStatus status) {
        return status != null ? DeliveryEntity.DeliveryStatusEnum.valueOf(status.name()) : null;
    }
}
