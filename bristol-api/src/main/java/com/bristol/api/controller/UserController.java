package com.bristol.api.controller;

import com.bristol.application.user.dto.ChangePasswordRequest;
import com.bristol.application.user.dto.UpdateUserProfileRequest;
import com.bristol.application.user.dto.UserDto;
import com.bristol.application.user.usecase.ChangePasswordUseCase;
import com.bristol.application.user.usecase.GetUserByIdUseCase;
import com.bristol.application.user.usecase.UpdateUserProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user endpoints.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        UserDto user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update user profile.
     */
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update user profile", description = "Update user's first and last name")
    public ResponseEntity<UserDto> updateUserProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UserDto user = updateUserProfileUseCase.execute(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * Change user password.
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Change password", description = "Change user's password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordUseCase.execute(id, request);
        return ResponseEntity.noContent().build();
    }
}
