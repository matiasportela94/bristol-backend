package com.bristol.domain.coupon;

import com.bristol.domain.shared.valueobject.Money;

/**
 * A concrete promotion application selected by the engine.
 */
public record PromotionApplication(
        Coupon coupon,
        Money discountAmount
) {
}
