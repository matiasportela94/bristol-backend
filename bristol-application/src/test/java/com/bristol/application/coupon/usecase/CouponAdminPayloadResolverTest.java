package com.bristol.application.coupon.usecase;

import com.bristol.application.coupon.dto.CouponBenefitPayload;
import com.bristol.application.coupon.dto.CouponScopePayload;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponScopeType;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponAdminPayloadResolverTest {

    private final CouponAdminPayloadResolver resolver = new CouponAdminPayloadResolver();

    @Test
    void shouldBuildStructuredCategoryScopeIntoLegacySelection() {
        CouponScopePayload scope = CouponScopePayload.builder()
                .type(CouponScopeType.CATEGORY)
                .categories(Set.of(ProductCategory.MERCHANDISING))
                .build();

        assertThat(resolver.resolveAppliesTo(CouponAppliesTo.ENTIRE_ORDER, scope))
                .isEqualTo(CouponAppliesTo.SPECIFIC_PRODUCTS);
        assertThat(resolver.resolveSelectedItems("[]", scope))
                .isEqualTo("[{\"category\":\"MERCHANDISING\"}]");
    }

    @Test
    void shouldBuildStructuredBuyXForYBenefitIntoLegacyRuleConfig() {
        CouponBenefitPayload benefit = CouponBenefitPayload.builder()
                .type(CouponBenefitType.BUY_X_FOR_Y)
                .buyQuantity(3)
                .payQuantity(2)
                .build();

        assertThat(resolver.resolveTriggerType(CouponTriggerType.NONE, benefit)).isEqualTo(CouponTriggerType.NONE);
        assertThat(resolver.resolveRuleConfig("{}", benefit))
                .isEqualTo("{\"type\":\"buy_x_for_y\",\"buyX\":3,\"forY\":2}");
    }

    @Test
    void shouldRejectInvalidStructuredProductScope() {
        CouponScopePayload scope = CouponScopePayload.builder()
                .type(CouponScopeType.SPECIFIC_PRODUCT)
                .build();

        assertThatThrownBy(() -> resolver.resolveSelectedItems("[]", scope))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Product-scoped promotions require at least one product or variant");
    }
}
