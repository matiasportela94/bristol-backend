package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductPromotionDto;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponBenefit;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponScope;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.product.BaseProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves catalog-visible product promotions so the frontend can surface them directly in the catalog UX.
 */
@Service
@RequiredArgsConstructor
public class ProductCatalogPromotionService {

    private final CouponRepository couponRepository;

    public List<ProductPromotionDto> resolveForProduct(BaseProduct product) {
        return resolveForProducts(List.of(product))
                .getOrDefault(product.getId().getValue().toString(), List.of());
    }

    public Map<String, List<ProductPromotionDto>> resolveForProducts(List<BaseProduct> products) {
        if (products == null || products.isEmpty()) {
            return Map.of();
        }

        List<Coupon> catalogCoupons = couponRepository.findActive().stream()
                .filter(this::isCatalogVisiblePromotion)
                .sorted(Comparator.comparingInt(Coupon::getPriority)
                        .reversed()
                        .thenComparing(Coupon::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        Map<String, List<ProductPromotionDto>> promotionsByProductId = new LinkedHashMap<>();
        for (BaseProduct product : products) {
            List<ProductPromotionDto> promotions = catalogCoupons.stream()
                    .filter(coupon -> matchesProduct(coupon, product))
                    .map(this::toDto)
                    .toList();
            promotionsByProductId.put(product.getId().getValue().toString(), promotions);
        }

        return promotionsByProductId;
    }

    private boolean isCatalogVisiblePromotion(Coupon coupon) {
        return coupon.isActive()
                && coupon.getDiscountType() == CouponDiscountType.PRODUCT
                && !coupon.isForSpecificCustomers();
    }

    private boolean matchesProduct(Coupon coupon, BaseProduct product) {
        CouponScope scope = coupon.getScope();
        String productId = product.getId().getValue().toString();

        return switch (scope.getType()) {
            case ENTIRE_ORDER -> true;
            case SPECIFIC_PRODUCT -> scope.getProductIds().contains(productId);
            case CATEGORY -> scope.getCategories().contains(product.getCategory());
            case SUBCATEGORY -> product.getSubcategory() != null && scope.getSubcategories().contains(product.getSubcategory());
            case BEER_TYPE -> product.getBeerType() != null && scope.getBeerTypes().contains(product.getBeerType());
            case MANUAL_SELECTION, COLLECTION -> false;
        };
    }

    private ProductPromotionDto toDto(Coupon coupon) {
        CouponBenefit benefit = coupon.getBenefit();

        return ProductPromotionDto.builder()
                .id(coupon.getId().getValue().toString())
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .badgeText(buildBadgeText(benefit))
                .detailsText(buildDetailsText(coupon, benefit))
                .code(coupon.getCode())
                .method(coupon.getMethod())
                .scopeType(coupon.getScope().getType())
                .benefitType(benefit.getType())
                .valueType(benefit.getValueType())
                .value(benefit.getValue())
                .priority(coupon.getPriority())
                .minimumAmount(coupon.getMinimumAmount() != null ? coupon.getMinimumAmount().getAmount() : null)
                .minimumQuantity(coupon.getMinimumQuantity())
                .buyQuantity(benefit.getBuyQuantity())
                .freeQuantity(benefit.getFreeQuantity())
                .payQuantity(benefit.getPayQuantity())
                .thresholdQuantity(benefit.getThresholdQuantity())
                .triggerProductId(coupon.getTriggerProductId())
                .triggerProductName(coupon.getTriggerProductName())
                .triggerQuantity(benefit.getTriggerQuantity())
                .build();
    }

    private String buildBadgeText(CouponBenefit benefit) {
        return switch (benefit.getType()) {
            case PRODUCT_PERCENTAGE, TRIGGERED_PRODUCT_DISCOUNT -> formatPercentage(benefit.getValue()) + " off";
            case PRODUCT_FIXED_AMOUNT -> formatMoney(benefit.getValue()) + " off";
            case BUY_X_GET_Y -> buildBuyXGetYText(benefit);
            case BUY_X_FOR_Y -> buildBuyXForYText(benefit);
            case PERCENTAGE_ON_QUANTITY -> buildThresholdDiscountText(benefit, true);
            case QUANTITY_RULE -> buildThresholdDiscountText(benefit, benefit.getValueType() == CouponValueType.PERCENTAGE);
            default -> "Special offer";
        };
    }

    private String buildDetailsText(Coupon coupon, CouponBenefit benefit) {
        List<String> details = new ArrayList<>();

        if (benefit.getType() == CouponBenefitType.BUY_X_GET_Y) {
            details.add(buildBuyXGetYDetails(benefit));
        } else if (benefit.getType() == CouponBenefitType.BUY_X_FOR_Y) {
            details.add(buildBuyXForYDetails(benefit));
        } else if (coupon.requiresCode() && coupon.getCode() != null && !coupon.getCode().isBlank()) {
            details.add("Use code " + coupon.getCode() + " at checkout");
        } else {
            details.add("Applied automatically at checkout");
        }

        if (benefit.getType() == CouponBenefitType.TRIGGERED_PRODUCT_DISCOUNT) {
            if (coupon.getTriggerProductName() != null && !coupon.getTriggerProductName().isBlank()) {
                details.add("Requires " + coupon.getTriggerProductName());
            } else {
                details.add("Requires a qualifying product");
            }

            if (benefit.getTriggerQuantity() != null) {
                details.add("Qty " + benefit.getTriggerQuantity() + "+");
            }
        }

        if (coupon.getMinimumQuantity() != null) {
            details.add("Min quantity " + coupon.getMinimumQuantity());
        }

        if (coupon.getMinimumAmount() != null) {
            details.add("Min order " + formatMoney(coupon.getMinimumAmount().getAmount()));
        }

        return String.join(". ", details);
    }

    private String buildBuyXGetYText(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer freeQuantity = benefit.getFreeQuantity();
        if (buyQuantity == null || freeQuantity == null) {
            return "Bundle offer";
        }
        return (buyQuantity + freeQuantity) + "x" + buyQuantity;
    }

    private String buildBuyXForYText(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer payQuantity = benefit.getPayQuantity();
        if (buyQuantity == null || payQuantity == null) {
            return "Bundle offer";
        }
        return buyQuantity + "x" + payQuantity;
    }

    private String buildBuyXGetYDetails(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer freeQuantity = benefit.getFreeQuantity();
        if (buyQuantity == null || freeQuantity == null) {
            return "Applied automatically at checkout";
        }
        return "Take " + (buyQuantity + freeQuantity) + ", pay " + buyQuantity;
    }

    private String buildBuyXForYDetails(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer payQuantity = benefit.getPayQuantity();
        if (buyQuantity == null || payQuantity == null) {
            return "Applied automatically at checkout";
        }
        return "Take " + buyQuantity + ", pay " + payQuantity;
    }

    private String buildThresholdDiscountText(CouponBenefit benefit, boolean percentage) {
        String discountText = percentage
                ? formatPercentage(benefit.getValue()) + " off"
                : formatMoney(benefit.getValue()) + " off";

        Integer thresholdQuantity = benefit.getThresholdQuantity();
        if (thresholdQuantity == null) {
            return discountText;
        }

        return discountText + " on " + thresholdQuantity + "+";
    }

    private String formatPercentage(BigDecimal value) {
        return formatNumber(value) + "%";
    }

    private String formatMoney(BigDecimal value) {
        return "$" + formatNumber(value);
    }

    private String formatNumber(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
