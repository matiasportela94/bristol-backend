package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.DistributorStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDistributorStatsUseCase {

    private final StatisticsQueryService statisticsQueryService;

    @Transactional(readOnly = true)
    public DistributorStatsDto execute(String distributorId) {
        return statisticsQueryService.getDistributorStats(distributorId);
    }
}
