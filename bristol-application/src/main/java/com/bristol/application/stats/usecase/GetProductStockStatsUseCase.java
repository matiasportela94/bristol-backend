package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.StockStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProductStockStatsUseCase {

    private final StatisticsQueryService statisticsQueryService;

    @Transactional(readOnly = true)
    public List<StockStatDto> execute() {
        return statisticsQueryService.getProductStockStats();
    }
}
