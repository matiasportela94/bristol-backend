package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductPromotionDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponAppliesTo;
import com.bristol.domain.coupon.CouponCustomerEligibility;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponMethod;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.domain.coupon.CouponTriggerType;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductCatalogPromotionServiceTest {

    @Test
    void resolveForProductsShouldMatchScopeAndSortByPriority() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        ProductCatalogPromotionService service = new ProductCatalogPromotionService(couponRepository);

        Product beer = sampleProduct(
                "Cerveza IPA",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                BeerType.IPA
        );
        Product merch = sampleProduct(
                "Remera Bristol",
                ProductCategory.MERCHANDISING,
                ProductSubcategory.REMERA,
                null
        );

        Coupon specificProductCoupon = sampleProductCoupon(
                "IPA Flash",
                null,
                CouponMethod.AUTOMATIC,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(20),
                "[{\"productId\":\"" + beer.getId().getValue() + "\"}]",
                20,
                CouponCustomerEligibility.EVERYONE,
                CouponTriggerType.NONE,
                null,
                null,
                "{}"
        );
        Coupon beerTypeCoupon = sampleProductCoupon(
                "IPA Week",
                "IPAWEEK",
                CouponMethod.CODE,
                CouponValueType.FIXED,
                BigDecimal.valueOf(500),
                "[{\"beerType\":\"IPA\"}]",
                10,
                CouponCustomerEligibility.EVERYONE,
                CouponTriggerType.NONE,
                null,
                null,
                "{}"
        );
        Coupon subcategoryCoupon = sampleProductCoupon(
                "Shirt Deal",
                null,
                CouponMethod.AUTOMATIC,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(15),
                "[{\"subcategory\":\"REMERA\"}]",
                8,
                CouponCustomerEligibility.EVERYONE,
                CouponTriggerType.NONE,
                null,
                null,
                "{}"
        );
        Coupon specificCustomerCoupon = sampleProductCoupon(
                "Private IPA",
                "PRIVATEIPA",
                CouponMethod.CODE,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(50),
                "[{\"productId\":\"" + beer.getId().getValue() + "\"}]",
                99,
                CouponCustomerEligibility.SPECIFIC_CUSTOMERS,
                CouponTriggerType.NONE,
                null,
                null,
                "{}"
        );
        Coupon orderCoupon = Coupon.create(
                "Order Wide",
                "ORDER10",
                "Order Wide",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                LocalDate.parse("2026-04-10"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-10T10:00:00Z")
        ).reconfigure(
                "Order Wide",
                "ORDER10",
                "Order Wide",
                CouponMethod.CODE,
                CouponDiscountType.ORDER,
                CouponValueType.PERCENTAGE,
                BigDecimal.TEN,
                CouponAppliesTo.ENTIRE_ORDER,
                "[]",
                CouponCustomerEligibility.EVERYONE,
                null,
                null,
                false,
                null,
                false,
                null,
                false,
                true,
                true,
                LocalDate.parse("2026-04-10"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                50,
                CouponTriggerType.NONE,
                null,
                null,
                false,
                "[]",
                "{}",
                Instant.parse("2026-04-10T10:00:00Z")
        );

        when(couponRepository.findActive()).thenReturn(List.of(
                specificProductCoupon,
                beerTypeCoupon,
                subcategoryCoupon,
                specificCustomerCoupon,
                orderCoupon
        ));

        Map<String, List<ProductPromotionDto>> promotionsByProductId = service.resolveForProducts(List.of(beer, merch));

        assertThat(promotionsByProductId.get(beer.getId().getValue().toString()))
                .extracting(ProductPromotionDto::getTitle)
                .containsExactly("IPA Flash", "IPA Week");
        assertThat(promotionsByProductId.get(beer.getId().getValue().toString()))
                .extracting(ProductPromotionDto::getBadgeText)
                .containsExactly("20% off", "$500 off");
        assertThat(promotionsByProductId.get(beer.getId().getValue().toString()))
                .extracting(ProductPromotionDto::getDetailsText)
                .containsExactly("Applied automatically at checkout", "Use code IPAWEEK at checkout");

        assertThat(promotionsByProductId.get(merch.getId().getValue().toString()))
                .extracting(ProductPromotionDto::getTitle)
                .containsExactly("Shirt Deal");
    }

    @Test
    void resolveForProductShouldExposeFrontendFriendlyTextsForAdvancedPromotions() {
        CouponRepository couponRepository = mock(CouponRepository.class);
        ProductCatalogPromotionService service = new ProductCatalogPromotionService(couponRepository);

        Product product = sampleProduct(
                "Lager Bristol",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                BeerType.LAGER
        );
        Product triggerProduct = sampleProduct(
                "IPA Bristol",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                BeerType.IPA
        );

        Coupon triggeredCoupon = sampleProductCoupon(
                "Friend Combo",
                null,
                CouponMethod.AUTOMATIC,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(50),
                "[{\"productId\":\"" + product.getId().getValue() + "\"}]",
                15,
                CouponCustomerEligibility.EVERYONE,
                CouponTriggerType.PRODUCT_PURCHASE,
                triggerProduct.getId().getValue().toString(),
                "IPA Bristol",
                "{\"triggerQuantity\":2}"
        );
        Coupon buyXGetYCoupon = sampleProductCoupon(
                "3x2 Lager",
                "LAGER3X2",
                CouponMethod.CODE,
                CouponValueType.PERCENTAGE,
                BigDecimal.valueOf(100),
                "[{\"productId\":\"" + product.getId().getValue() + "\"}]",
                10,
                CouponCustomerEligibility.EVERYONE,
                CouponTriggerType.BUY_X_GET_Y,
                null,
                null,
                "{\"buyX\":2,\"getY\":1}"
        );

        when(couponRepository.findActive()).thenReturn(List.of(triggeredCoupon, buyXGetYCoupon));

        List<ProductPromotionDto> promotions = service.resolveForProduct(product);

        assertThat(promotions)
                .extracting(ProductPromotionDto::getBadgeText)
                .containsExactly("50% off", "3x2");
        assertThat(promotions)
                .extracting(ProductPromotionDto::getDetailsText)
                .containsExactly(
                        "Applied automatically at checkout. Requires IPA Bristol. Qty 2+",
                        "Take 3, pay 2"
                );
    }

    private static Product sampleProduct(
            String name,
            ProductCategory category,
            ProductSubcategory subcategory,
            BeerType beerType
    ) {
        return Product.builder()
                .id(ProductId.generate())
                .name(name)
                .description(name)
                .category(category)
                .subcategory(subcategory)
                .beerType(beerType)
                .basePrice(Money.of(1000))
                .stockQuantity(10)
                .lowStockThreshold(2)
                .featured(false)
                .createdAt(Instant.parse("2026-04-10T10:00:00Z"))
                .updatedAt(Instant.parse("2026-04-10T10:00:00Z"))
                .build();
    }

    private static Coupon sampleProductCoupon(
            String title,
            String code,
            CouponMethod method,
            CouponValueType valueType,
            BigDecimal value,
            String selectedItems,
            int priority,
            CouponCustomerEligibility customerEligibility,
            CouponTriggerType triggerType,
            String triggerProductId,
            String triggerProductName,
            String ruleConfig
    ) {
        return Coupon.create(
                title,
                code,
                title,
                method,
                CouponDiscountType.PRODUCT,
                valueType,
                value,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                LocalDate.parse("2026-04-10"),
                LocalTime.MIDNIGHT,
                UserId.generate(),
                Instant.parse("2026-04-10T10:00:00Z")
        ).reconfigure(
                title,
                code,
                title,
                method,
                CouponDiscountType.PRODUCT,
                valueType,
                value,
                CouponAppliesTo.SPECIFIC_PRODUCTS,
                selectedItems,
                customerEligibility,
                null,
                null,
                false,
                null,
                false,
                null,
                true,
                true,
                true,
                LocalDate.parse("2026-04-10"),
                LocalTime.MIDNIGHT,
                false,
                null,
                null,
                CouponStatus.ACTIVE,
                priority,
                triggerType,
                triggerProductId,
                triggerProductName,
                false,
                "[]",
                ruleConfig,
                Instant.parse("2026-04-10T10:00:00Z")
        );
    }
}
