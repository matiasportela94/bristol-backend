package com.bristol.application.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDto {
    private String month;
    private List<MonthlyRankingDto> rankings;
    private Integer currentUserPosition;
}
