package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Re-evaluates manual and automatic promotions for an existing order.
 */
@Service
@RequiredArgsConstructor
public class RepriceOrderPromotionsUseCase {

    private final OrderPromotionApplicationService orderPromotionApplicationService;

    @Transactional
    public OrderDto execute(String orderId) {
        return orderPromotionApplicationService.repriceOrder(orderId);
    }
}
