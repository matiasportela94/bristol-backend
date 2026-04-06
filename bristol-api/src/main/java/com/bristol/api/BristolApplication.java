package com.bristol.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Bristol Platform - Main Spring Boot Application
 * Version: 3.0.0
 *
 * E-commerce platform for craft beer distribution with:
 * - User management and authentication
 * - Product catalog with variants and reviews
 * - Shopping cart and order management
 * - Coupon system with advanced rules
 * - Mercado Pago payment integration
 * - AFIP invoicing
 * - Delivery scheduling
 */
@SpringBootApplication(scanBasePackages = {
        "com.bristol.api",
        "com.bristol.infrastructure",
        "com.bristol.application"
})
@EnableJpaRepositories(basePackages = "com.bristol.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.bristol.infrastructure.persistence.entity")
public class BristolApplication {

    public static void main(String[] args) {
        SpringApplication.run(BristolApplication.class, args);
    }
}
