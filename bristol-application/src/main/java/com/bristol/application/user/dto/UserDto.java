package com.bristol.application.user.dto;

import com.bristol.domain.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for User information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Instant createdAt;
    private Instant updatedAt;
}
