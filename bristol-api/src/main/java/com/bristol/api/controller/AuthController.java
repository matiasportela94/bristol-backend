package com.bristol.api.controller;

import com.bristol.application.auth.dto.AuthResponse;
import com.bristol.application.auth.dto.LoginRequest;
import com.bristol.application.auth.dto.RegisterRequest;
import com.bristol.application.auth.usecase.LoginUseCase;
import com.bristol.application.auth.usecase.RegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    /**
     * User login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * User registration endpoint.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
