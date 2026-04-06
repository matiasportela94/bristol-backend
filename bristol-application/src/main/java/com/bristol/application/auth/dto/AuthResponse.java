package com.bristol.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing JWT token and user info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean isDistributor;

    public static AuthResponse of(String token, UserDto user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isDistributor(user.isDistributor())
                .build();
    }
}
