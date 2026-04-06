package com.bristol.application.auth.usecase;

import com.bristol.application.auth.dto.AuthResponse;
import com.bristol.application.auth.dto.RegisterRequest;
import com.bristol.application.auth.dto.UserDto;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case for user registration.
 */
@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator tokenGenerator;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Hash password
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // Create user
        User user = User.create(
                request.getEmail(),
                passwordHash,
                request.getFirstName(),
                request.getLastName(),
                UserRole.USER,
                Instant.now()
        );

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = tokenGenerator.generateTokenFromUsername(savedUser.getEmail());

        // Build response
        UserDto userDto = UserDto.builder()
                .id(savedUser.getId().getValue().toString())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phone(savedUser.getPhone())
                .dateOfBirth(savedUser.getDateOfBirth())
                .role(savedUser.getRole().name())
                .isDistributor(savedUser.isDistributor())
                .build();

        return AuthResponse.of(token, userDto);
    }
}
