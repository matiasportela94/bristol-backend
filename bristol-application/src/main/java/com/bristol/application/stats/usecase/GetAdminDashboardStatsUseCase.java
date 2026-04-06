package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.AdminDashboardStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminDashboardStatsUseCase {

    private final StatisticsQueryService statisticsQueryService;

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto execute() {
        return statisticsQueryService.getAdminDashboardStats();
    }
}
