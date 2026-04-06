package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.RankingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMonthlyRankingUseCase {

    private final StatisticsQueryService statisticsQueryService;

    @Transactional(readOnly = true)
    public RankingResponseDto execute(String month, String distributorId, Integer limit) {
        return statisticsQueryService.getMonthlyRanking(month, distributorId, limit);
    }
}
