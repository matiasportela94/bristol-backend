package com.bristol.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine.
 * Optimizes performance for frequently-accessed, rarely-changing data.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache names used throughout the application.
     */
    public static final String CACHE_BEER_STYLES = "beerStyles";
    public static final String CACHE_MERCH_TYPES = "merchTypes";
    public static final String CACHE_SPECIAL_TYPES = "specialTypes";
    public static final String CACHE_DELIVERY_ZONES = "deliveryZones";
    public static final String CACHE_FEATURED_PRODUCTS = "featuredProducts";
    public static final String CACHE_ACTIVE_COUPONS = "activeCoupons";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CACHE_BEER_STYLES,
                CACHE_MERCH_TYPES,
                CACHE_SPECIAL_TYPES,
                CACHE_DELIVERY_ZONES,
                CACHE_FEATURED_PRODUCTS,
                CACHE_ACTIVE_COUPONS
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000) // Maximum 1000 entries per cache
                .expireAfterWrite(30, TimeUnit.MINUTES) // Expire after 30 minutes
                .recordStats(); // Enable statistics for monitoring
    }
}
