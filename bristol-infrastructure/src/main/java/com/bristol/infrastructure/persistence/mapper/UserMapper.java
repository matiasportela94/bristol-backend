package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRole;
import com.bristol.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for User domain <-> UserEntity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", expression = "java(toUserId(entity.getId()))")
    @Mapping(target = "role", expression = "java(toDomainRole(entity.getRole()))")
    @Mapping(target = "isDistributor", expression = "java(Boolean.TRUE.equals(entity.getIsDistributor()))")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", expression = "java(user.getId().getValue())")
    @Mapping(target = "role", expression = "java(toEntityRole(user.getRole()))")
    @Mapping(target = "isDistributor", expression = "java(user.isDistributor())")
    UserEntity toEntity(User user);

    default UserId toUserId(java.util.UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default UserRole toDomainRole(UserEntity.UserRoleEnum roleEnum) {
        return roleEnum != null ? UserRole.valueOf(roleEnum.name()) : null;
    }

    default UserEntity.UserRoleEnum toEntityRole(UserRole role) {
        return role != null ? UserEntity.UserRoleEnum.valueOf(role.name()) : null;
    }
}
