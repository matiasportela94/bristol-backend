package com.bristol.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the API.
 */
@Configuration
public class CorsConfig {

    @Value("${bristol.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${bristol.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${bristol.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${bristol.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins from configuration
        configuration.setAllowedOrigins(parseCommaSeparated(allowedOrigins));

        // Set allowed methods
        configuration.setAllowedMethods(parseCommaSeparated(allowedMethods));

        // Set allowed headers
        if ("*".equals(allowedHeaders.trim())) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(parseCommaSeparated(allowedHeaders));
        }

        // Set allow credentials
        configuration.setAllowCredentials(allowCredentials);

        // Expose Authorization header
        configuration.setExposedHeaders(List.of("Authorization"));

        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private List<String> parseCommaSeparated(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
