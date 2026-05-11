package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper to calculate distributor statistics from orders.
 * Uses direct SQL queries for performance.
 */
@Component
@RequiredArgsConstructor
public class DistributorStatsCalculator {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Calculate total number of PAID orders for a distributor's userId.
     */
    public int calculateTotalOrders(UserId userId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE user_id = ? AND order_status = 'PAID'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId.getValue());
        return count != null ? count : 0;
    }
}
