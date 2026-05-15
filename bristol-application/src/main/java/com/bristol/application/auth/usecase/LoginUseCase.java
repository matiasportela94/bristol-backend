package com.bristol.application.auth.usecase;

import com.bristol.application.auth.dto.AuthResponse;
import com.bristol.application.auth.dto.LoginRequest;
import com.bristol.application.auth.dto.UserDto;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for user login.
 */
@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenGenerator tokenGenerator;
    private final TimeProvider timeProvider;

    @Transactional
    public AuthResponse execute(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = tokenGenerator.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.save(user.recordSignIn(timeProvider.now()));

        UserDto userDto = UserDto.builder()
                .id(user.getId().getValue().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole().name())
                .isDistributor(user.isDistributor())
                .distributorId(user.getDistributorId() != null ? user.getDistributorId().getValue().toString() : null)
                .branchId(user.getBranchId() != null ? user.getBranchId().getValue().toString() : null)
                .build();

        return AuthResponse.of(token, userDto);
    }
}
