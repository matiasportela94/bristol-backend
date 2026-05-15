package com.bristol.application.user.usecase;

import com.bristol.application.user.dto.UserDto;
import com.bristol.domain.user.User;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between User domain entity and UserDto.
 */
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId().getValue().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .isDistributor(user.isDistributor())
                .distributorId(user.getDistributorId() != null ? user.getDistributorId().getValue().toString() : null)
                .branchId(user.getBranchId() != null ? user.getBranchId().getValue().toString() : null)
                .lastSignInAt(user.getLastSignInAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
