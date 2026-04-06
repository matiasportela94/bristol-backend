package com.bristol.application.auth.usecase;

import org.springframework.security.core.Authentication;

/**
 * Port for JWT token generation.
 * Implemented by infrastructure layer.
 */
public interface JwtTokenGenerator {

    String generateToken(Authentication authentication);

    String generateTokenFromUsername(String username);
}
