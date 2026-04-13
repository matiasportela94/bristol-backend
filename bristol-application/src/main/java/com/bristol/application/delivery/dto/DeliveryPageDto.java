package com.bristol.application.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPageDto {
    private List<DeliveryDto> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private long scheduledCount;
    private long deliveredCount;
    private long cancelledCount;
}
