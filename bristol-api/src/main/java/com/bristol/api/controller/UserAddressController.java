package com.bristol.api.controller;

import com.bristol.application.address.dto.CreateUserAddressRequest;
import com.bristol.application.address.dto.UpdateUserAddressRequest;
import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.application.address.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-addresses")
@RequiredArgsConstructor
@Tag(name = "User Addresses", description = "User address management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserAddressController {
    private final CreateUserAddressUseCase createUserAddressUseCase;
    private final GetUserAddressesUseCase getUserAddressesUseCase;
    private final UpdateUserAddressUseCase updateUserAddressUseCase;
    private final DeleteUserAddressUseCase deleteUserAddressUseCase;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get user addresses", description = "Get all addresses for a user")
    public ResponseEntity<List<UserAddressDto>> getUserAddresses(@PathVariable String userId) {
        return ResponseEntity.ok(getUserAddressesUseCase.execute(userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Create address", description = "Create a new address for a user")
    public ResponseEntity<UserAddressDto> createAddress(@Valid @RequestBody CreateUserAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserAddressUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<UserAddressDto> updateAddress(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserAddressRequest request
    ) {
        return ResponseEntity.ok(updateUserAddressUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Delete address", description = "Delete an address")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
        deleteUserAddressUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
