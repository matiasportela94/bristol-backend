package com.bristol.api.controller;

import com.bristol.application.distributor.usecase.DistributorAccessService;
import com.bristol.application.stats.dto.AdminDashboardStatsDto;
import com.bristol.application.stats.dto.DistributorStatsDto;
import com.bristol.application.stats.dto.RankingResponseDto;
import com.bristol.application.stats.dto.StockStatDto;
import com.bristol.application.stats.usecase.GetAdminDashboardStatsUseCase;
import com.bristol.application.stats.usecase.GetDistributorStatsUseCase;
import com.bristol.application.stats.usecase.GetMonthlyRankingUseCase;
import com.bristol.application.stats.usecase.GetProductStockStatsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "Statistics endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class StatsController {

    private final GetAdminDashboardStatsUseCase getAdminDashboardStatsUseCase;
    private final GetDistributorStatsUseCase getDistributorStatsUseCase;
    private final GetMonthlyRankingUseCase getMonthlyRankingUseCase;
    private final GetProductStockStatsUseCase getProductStockStatsUseCase;
    private final DistributorAccessService distributorAccessService;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard statistics", description = "Retrieve overview statistics for the admin dashboard")
    public ResponseEntity<AdminDashboardStatsDto> getAdminDashboardStats() {
        return ResponseEntity.ok(getAdminDashboardStatsUseCase.execute());
    }

    @GetMapping("/distributors/{distributorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get distributor statistics", description = "Retrieve statistics for a specific distributor")
    public ResponseEntity<DistributorStatsDto> getDistributorStats(
            @PathVariable String distributorId,
            Authentication authentication
    ) {
        resolveAccessibleDistributor(distributorId, authentication);
        return ResponseEntity.ok(getDistributorStatsUseCase.execute(distributorId));
    }

    @GetMapping("/rankings/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get monthly ranking", description = "Retrieve monthly ranking of distributors based on real order data")
    public ResponseEntity<RankingResponseDto> getMonthlyRanking(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String distributorId,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(getMonthlyRankingUseCase.execute(month, distributorId, limit));
    }

    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get stock statistics", description = "Retrieve stock totals grouped by beer type")
    public ResponseEntity<List<StockStatDto>> getProductStockStats() {
        return ResponseEntity.ok(getProductStockStatsUseCase.execute());
    }

    private void resolveAccessibleDistributor(String distributorId, Authentication authentication) {
        distributorAccessService.getAccessibleDistributor(
                distributorId,
                authentication.getName(),
                distributorAccessService.isAdmin(authentication.getAuthorities())
        );
    }
}
