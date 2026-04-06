package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.user.UserId;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.infrastructure.persistence.entity.UserAddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/**
 * MapStruct mapper for UserAddress domain object and UserAddressEntity.
 */
@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    @Mapping(target = "id", expression = "java(toUserAddressId(entity.getId()))")
    @Mapping(target = "userId", expression = "java(toUserId(entity.getUserId()))")
    @Mapping(target = "deliveryZoneId", expression = "java(toDeliveryZoneId(entity.getDeliveryZoneId()))")
    @Mapping(target = "isDefault", expression = "java(Boolean.TRUE.equals(entity.getIsDefault()))")
    UserAddress toDomain(UserAddressEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "userId", expression = "java(toUUID(domain.getUserId()))")
    @Mapping(target = "deliveryZoneId", expression = "java(toUUID(domain.getDeliveryZoneId()))")
    @Mapping(target = "isDefault", expression = "java(domain.isDefault())")
    @Mapping(target = "deletedAt", ignore = true)
    UserAddressEntity toEntity(UserAddress domain);

    default UserAddressId toUserAddressId(UUID uuid) {
        return uuid != null ? new UserAddressId(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default DeliveryZoneId toDeliveryZoneId(UUID uuid) {
        return uuid != null ? new DeliveryZoneId(uuid) : null;
    }

    default UUID toUUID(UserAddressId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DeliveryZoneId id) {
        return id != null ? id.getValue() : null;
    }
}
