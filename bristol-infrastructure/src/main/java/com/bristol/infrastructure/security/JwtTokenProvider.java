package com.bristol.infrastructure.security;

import com.bristol.application.auth.usecase.JwtTokenGenerator;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens.
 * Implements JwtTokenGenerator port from application layer.
 */
@Slf4j
@Component
public class JwtTokenProvider implements JwtTokenGenerator {

    private final SecretKey secretKey;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${bristol.jwt.secret}") String jwtSecret,
            @Value("${bristol.jwt.expiration}") long jwtExpirationInMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Generate JWT token from Authentication object.
     */
    @Override
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());
    }

    /**
     * Generate JWT token from username (email).
     */
    @Override
    public String generateTokenFromUsername(String username) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationInMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get username (email) from JWT token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}
