package com.bristol.domain.coupon;

import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderCalculationService;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Centralized promotion evaluation for the current coupon-backed model.
 */
public class PromotionEngine {

    private static final Pattern INTEGER_FIELD_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)");
    private static final Comparator<Coupon> COUPON_PRIORITY_COMPARATOR = Comparator
            .comparingInt(Coupon::getPriority)
            .reversed()
            .thenComparing(Coupon::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(coupon -> coupon.getId().getValue());

    private final CouponValidationService couponValidationService;
    private final OrderCalculationService orderCalculationService;

    public PromotionEngine(TimeProvider timeProvider) {
        this(new CouponValidationService(timeProvider), new OrderCalculationService());
    }

    public PromotionEngine(TimeProvider timeProvider, CouponRedemptionRepository couponRedemptionRepository) {
        this(new CouponValidationService(timeProvider, couponRedemptionRepository), new OrderCalculationService());
    }

    PromotionEngine(
            CouponValidationService couponValidationService,
            OrderCalculationService orderCalculationService
    ) {
        this.couponValidationService = couponValidationService;
        this.orderCalculationService = orderCalculationService;
    }

    public void validateCoupon(Order order, Coupon coupon) {
        validateCoupon(order, coupon, null);
    }

    public void validateCoupon(Order order, Coupon coupon, String userEmail) {
        validateCandidate(order, coupon, userEmail);
    }

    public PromotionEvaluationResult evaluate(Order order, List<Coupon> candidateCoupons) {
        return evaluate(order, candidateCoupons, null);
    }

    public PromotionEvaluationResult evaluate(Order order, List<Coupon> candidateCoupons, String userEmail) {
        List<Coupon> eligibleCoupons = new ArrayList<>();
        for (Coupon coupon : candidateCoupons) {
            if (coupon == null) {
                continue;
            }
            try {
                validateCandidate(order, coupon, userEmail);
                eligibleCoupons.add(coupon);
            } catch (ValidationException ignored) {
                // Stale or incompatible already-applied promotions are dropped during repricing.
            }
        }

        eligibleCoupons.sort(COUPON_PRIORITY_COMPARATOR);

        PromotionApplication orderPromotion = null;
        PromotionApplication shippingPromotion = null;
        List<PromotionApplication> productPromotions = new ArrayList<>();
        List<OrderItem> repricedItems = clearItemDiscounts(order.getItems());

        for (Coupon coupon : eligibleCoupons) {
            switch (coupon.getDiscountType()) {
                case PRODUCT -> {
                    if (!canCombine(coupon, orderPromotion, shippingPromotion, productPromotions)) {
                        continue;
                    }

                    ProductPromotionOutcome productPromotionOutcome = evaluateProductPromotion(repricedItems, coupon);
                    if (productPromotionOutcome != null) {
                        productPromotions.add(new PromotionApplication(coupon, productPromotionOutcome.discountAmount()));
                        repricedItems = productPromotionOutcome.items();
                    }
                }
                case ORDER -> {
                    if (orderPromotion == null && canCombine(coupon, orderPromotion, shippingPromotion, productPromotions)) {
                        orderPromotion = selectOrderPromotion(order, repricedItems, coupon);
                    }
                }
                case SHIPPING -> {
                    if (shippingPromotion == null && canCombine(coupon, orderPromotion, shippingPromotion, productPromotions)) {
                        shippingPromotion = selectShippingPromotion(order, coupon);
                    }
                }
            }
        }

        return new PromotionEvaluationResult(orderPromotion, shippingPromotion, List.copyOf(productPromotions), repricedItems);
    }

    private PromotionApplication selectOrderPromotion(Order order, List<OrderItem> repricedItems, Coupon coupon) {
        Money repricedSubtotal = repricedItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.zero(), Money::add);

        Order calculationContext = order.toBuilder()
                .items(new ArrayList<>(repricedItems))
                .subtotal(repricedSubtotal)
                .build();

        return new PromotionApplication(
                coupon,
                orderCalculationService.calculateOrderDiscount(calculationContext, coupon)
        );
    }

    private PromotionApplication selectShippingPromotion(Order order, Coupon coupon) {
        return new PromotionApplication(
                coupon,
                orderCalculationService.calculateShippingDiscount(order.getShippingCost(), coupon)
        );
    }

    private PromotionApplication selectProductPromotion(List<OrderItem> items, Coupon coupon) {
        List<OrderItem> matchingItems = findMatchingItems(items, coupon);
        if (matchingItems.isEmpty()) {
            return null;
        }

        Money totalDiscount = matchingItems.stream()
                .map(item -> orderCalculationService.calculateItemDiscount(item, coupon))
                .reduce(Money.zero(), Money::add);

        if (totalDiscount.isZero()) {
            return null;
        }

        return new PromotionApplication(coupon, totalDiscount);
    }

    private ProductPromotionOutcome evaluateProductPromotion(List<OrderItem> items, Coupon coupon) {
        CouponBenefit benefit = coupon.getBenefit();
        List<Integer> matchingIndexes = findMatchingIndexes(items, coupon);
        matchingIndexes = matchingIndexes.stream()
                .filter(index -> items.get(index).getItemDiscountCouponId() == null)
                .toList();
        if (matchingIndexes.isEmpty()) {
            return null;
        }

        return switch (benefit.getType()) {
            case PRODUCT_PERCENTAGE, PRODUCT_FIXED_AMOUNT -> applyDirectProductPromotion(items, matchingIndexes, coupon);
            case TRIGGERED_PRODUCT_DISCOUNT -> applyTriggeredProductPromotion(items, matchingIndexes, coupon, benefit);
            case BUY_X_GET_Y -> applyBuyXGetYPromotion(items, matchingIndexes, coupon, benefit);
            case BUY_X_FOR_Y -> applyBuyXForYPromotion(items, matchingIndexes, coupon, benefit);
            case PERCENTAGE_ON_QUANTITY, QUANTITY_RULE -> applyThresholdProductPromotion(items, matchingIndexes, coupon, benefit);
            default -> null;
        };
    }

    private ProductPromotionOutcome applyDirectProductPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon
    ) {
        Map<Integer, Money> discounts = new HashMap<>();
        for (Integer index : matchingIndexes) {
            OrderItem item = items.get(index);
            Money discount = orderCalculationService.calculateItemDiscount(item, coupon);
            if (!discount.isZero()) {
                discounts.put(index, discount);
            }
        }
        return buildProductPromotionOutcome(items, discounts, coupon);
    }

    private ProductPromotionOutcome applyTriggeredProductPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon,
            CouponBenefit benefit
    ) {
        if (!hasTriggerMatch(items, coupon, benefit)) {
            return null;
        }

        Map<Integer, Money> discounts = new HashMap<>();
        for (Integer index : matchingIndexes) {
            OrderItem item = items.get(index);
            Money discount = calculateSimpleItemDiscount(item, coupon);
            if (!discount.isZero()) {
                discounts.put(index, discount);
            }
        }

        return buildProductPromotionOutcome(items, discounts, coupon);
    }

    private ProductPromotionOutcome applyBuyXGetYPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon,
            CouponBenefit benefit
    ) {
        Integer buyQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "buyX",
                "buyQuantity",
                "requiredQuantity"
        );
        Integer freeQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "getY",
                "freeQuantity",
                "discountQuantity"
        );

        if (buyQuantity == null || freeQuantity == null) {
            throw new ValidationException("Buy X get Y promotion is missing quantity configuration");
        }

        int totalQuantity = sumQuantity(items, matchingIndexes);
        int groupSize = buyQuantity + freeQuantity;
        if (groupSize <= 0) {
            throw new ValidationException("Buy X get Y promotion has an invalid quantity configuration");
        }

        int discountedUnits = (totalQuantity / groupSize) * freeQuantity;
        return applyFreeUnitPromotion(items, matchingIndexes, coupon, discountedUnits);
    }

    private ProductPromotionOutcome applyBuyXForYPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon,
            CouponBenefit benefit
    ) {
        Integer buyQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "buyX",
                "buyQuantity",
                "requiredQuantity",
                "quantity"
        );
        Integer payQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "forY",
                "payY",
                "payQuantity",
                "chargedQuantity"
        );

        if (buyQuantity == null || payQuantity == null || payQuantity >= buyQuantity) {
            throw new ValidationException("Buy X for Y promotion has an invalid quantity configuration");
        }

        int totalQuantity = sumQuantity(items, matchingIndexes);
        int discountedUnits = (totalQuantity / buyQuantity) * (buyQuantity - payQuantity);
        return applyFreeUnitPromotion(items, matchingIndexes, coupon, discountedUnits);
    }

    private ProductPromotionOutcome applyThresholdProductPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon,
            CouponBenefit benefit
    ) {
        Integer thresholdQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "minQuantity",
                "minimumQuantity",
                "thresholdQuantity",
                "quantity",
                "buyQuantity"
        );
        if (thresholdQuantity == null) {
            thresholdQuantity = coupon.getMinimumQuantity();
        }
        if (thresholdQuantity == null || thresholdQuantity <= 0) {
            throw new ValidationException("Quantity-based promotion is missing a threshold configuration");
        }

        if (sumQuantity(items, matchingIndexes) < thresholdQuantity) {
            return null;
        }

        Map<Integer, Money> discounts = new HashMap<>();
        for (Integer index : matchingIndexes) {
            OrderItem item = items.get(index);
            Money discount = calculateSimpleItemDiscount(item, coupon);
            if (!discount.isZero()) {
                discounts.put(index, discount);
            }
        }

        return buildProductPromotionOutcome(items, discounts, coupon);
    }

    private ProductPromotionOutcome applyFreeUnitPromotion(
            List<OrderItem> items,
            List<Integer> matchingIndexes,
            Coupon coupon,
            int discountedUnits
    ) {
        if (discountedUnits <= 0) {
            return null;
        }

        List<Integer> sortedIndexes = matchingIndexes.stream()
                .sorted(Comparator.comparing(index -> items.get(index).getPricePerUnit().getAmount()))
                .toList();

        Map<Integer, Money> discounts = new HashMap<>();
        int remainingUnits = discountedUnits;

        for (Integer index : sortedIndexes) {
            if (remainingUnits <= 0) {
                break;
            }

            OrderItem item = items.get(index);
            int applicableUnits = Math.min(item.getQuantity(), remainingUnits);
            if (applicableUnits > 0) {
                discounts.put(index, item.getPricePerUnit().multiply(applicableUnits));
                remainingUnits -= applicableUnits;
            }
        }

        return buildProductPromotionOutcome(items, discounts, coupon);
    }

    private ProductPromotionOutcome buildProductPromotionOutcome(
            List<OrderItem> items,
            Map<Integer, Money> discounts,
            Coupon coupon
    ) {
        if (discounts.isEmpty()) {
            return null;
        }

        List<OrderItem> updatedItems = new ArrayList<>(items.size());
        Money totalDiscount = Money.zero();

        for (int index = 0; index < items.size(); index++) {
            OrderItem item = items.get(index);
            Money discount = discounts.get(index);
            if (discount == null || discount.isZero()) {
                updatedItems.add(item);
                continue;
            }

            totalDiscount = totalDiscount.add(discount);
            updatedItems.add(item.applyItemDiscount(coupon.getId(), discount));
        }

        return totalDiscount.isZero() ? null : new ProductPromotionOutcome(updatedItems, totalDiscount);
    }

    private void validateCandidate(Order order, Coupon coupon, String userEmail) {
        couponValidationService.validateCouponForOrder(
                coupon,
                order.getSubtotal(),
                order.getItems().stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0).sum(),
                order.getUserId(),
                userEmail
        );
        validateSupportedScope(coupon);
        validateSupportedBenefit(coupon);
    }

    private void validateSupportedScope(Coupon coupon) {
        if ((coupon.getDiscountType() == CouponDiscountType.ORDER || coupon.getDiscountType() == CouponDiscountType.SHIPPING)
                && !coupon.getScope().isEntireOrder()) {
            throw new ValidationException("This coupon scope is not yet supported for order repricing");
        }

        if (coupon.getDiscountType() == CouponDiscountType.PRODUCT) {
            CouponScopeType scopeType = coupon.getScope().getType();
            if (scopeType != CouponScopeType.ENTIRE_ORDER
                    && scopeType != CouponScopeType.SPECIFIC_PRODUCT
                    && scopeType != CouponScopeType.MANUAL_SELECTION
                    && scopeType != CouponScopeType.CATEGORY
                    && scopeType != CouponScopeType.SUBCATEGORY
                    && scopeType != CouponScopeType.BEER_TYPE) {
                throw new ValidationException("This coupon scope is not yet supported for product repricing");
            }
        }
    }

    private void validateSupportedBenefit(Coupon coupon) {
        if (coupon.getBenefit().supportsDirectDiscountCalculation()) {
            return;
        }

        if (coupon.getDiscountType() == CouponDiscountType.PRODUCT) {
            switch (coupon.getBenefit().getType()) {
                case TRIGGERED_PRODUCT_DISCOUNT, BUY_X_GET_Y, BUY_X_FOR_Y, PERCENTAGE_ON_QUANTITY, QUANTITY_RULE -> {
                    return;
                }
                default -> {
                    // fall through to shared error below
                }
            }
        }

        throw new ValidationException("This coupon benefit requires PromotionEngine advanced evaluation");
    }

    private boolean canCombine(
            Coupon candidate,
            PromotionApplication orderPromotion,
            PromotionApplication shippingPromotion,
            List<PromotionApplication> productPromotions
    ) {
        return isCompatible(candidate, orderPromotion)
                && isCompatible(candidate, shippingPromotion)
                && productPromotions.stream().allMatch(existing -> isCompatible(candidate, existing));
    }

    private boolean isCompatible(Coupon candidate, PromotionApplication existingPromotion) {
        return existingPromotion == null || isCompatible(candidate, existingPromotion.coupon());
    }

    private boolean isCompatible(Coupon left, Coupon right) {
        if (left.getDiscountType() == CouponDiscountType.PRODUCT && right.getDiscountType() == CouponDiscountType.ORDER) {
            return left.canCombineWithOrderDiscounts() && right.canCombineWithProductDiscounts();
        }
        if (left.getDiscountType() == CouponDiscountType.ORDER && right.getDiscountType() == CouponDiscountType.PRODUCT) {
            return left.canCombineWithProductDiscounts() && right.canCombineWithOrderDiscounts();
        }
        if (left.getDiscountType() == CouponDiscountType.PRODUCT && right.getDiscountType() == CouponDiscountType.SHIPPING) {
            return left.canCombineWithShippingDiscounts() && right.canCombineWithProductDiscounts();
        }
        if (left.getDiscountType() == CouponDiscountType.SHIPPING && right.getDiscountType() == CouponDiscountType.PRODUCT) {
            return left.canCombineWithProductDiscounts() && right.canCombineWithShippingDiscounts();
        }
        if (left.getDiscountType() == CouponDiscountType.ORDER && right.getDiscountType() == CouponDiscountType.SHIPPING) {
            return left.canCombineWithShippingDiscounts() && right.canCombineWithOrderDiscounts();
        }
        if (left.getDiscountType() == CouponDiscountType.SHIPPING && right.getDiscountType() == CouponDiscountType.ORDER) {
            return left.canCombineWithOrderDiscounts() && right.canCombineWithShippingDiscounts();
        }
        if (left.getDiscountType() == right.getDiscountType()) {
            if (left.getDiscountType() == CouponDiscountType.PRODUCT) {
                // Product promotions are evaluated per item. We allow multiple product coupons
                // at cart level and rely on itemDiscountCouponId to prevent stacking on the same line.
                return true;
            }
            return false;
        }
        return true;
    }

    private List<OrderItem> clearItemDiscounts(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::clearItemDiscount)
                .toList();
    }

    private List<OrderItem> findMatchingItems(List<OrderItem> items, Coupon coupon) {
        return findMatchingIndexes(items, coupon).stream()
                .map(items::get)
                .toList();
    }

    private List<Integer> findMatchingIndexes(List<OrderItem> items, Coupon coupon) {
        return java.util.stream.IntStream.range(0, items.size())
                .filter(index -> matchesScope(items.get(index), coupon.getScope()))
                .boxed()
                .toList();
    }

    private int sumQuantity(List<OrderItem> items, List<Integer> matchingIndexes) {
        return matchingIndexes.stream()
                .map(items::get)
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    private Integer extractPositiveInteger(String rawRuleConfig, String... keys) {
        Matcher matcher = INTEGER_FIELD_PATTERN.matcher(rawRuleConfig != null ? rawRuleConfig : "{}");
        while (matcher.find()) {
            String key = matcher.group(1);
            for (String candidate : keys) {
                if (candidate.equalsIgnoreCase(key)) {
                    int value = Integer.parseInt(matcher.group(2));
                    return value > 0 ? value : null;
                }
            }
        }
        return null;
    }

    private Money calculateSimpleItemDiscount(OrderItem item, Coupon coupon) {
        Money itemSubtotal = item.getOriginalSubtotal();
        if (coupon.getValueType() == CouponValueType.PERCENTAGE) {
            return itemSubtotal.percentage(coupon.getValue());
        }

        Money fixedDiscount = Money.of(coupon.getValue());
        return itemSubtotal.isLessThan(fixedDiscount) ? itemSubtotal : fixedDiscount;
    }

    private boolean hasTriggerMatch(List<OrderItem> items, Coupon coupon, CouponBenefit benefit) {
        String triggerProductId = coupon.getTriggerProductId();
        String triggerProductName = coupon.getTriggerProductName();

        if ((triggerProductId == null || triggerProductId.isBlank())
                && (triggerProductName == null || triggerProductName.isBlank())) {
            throw new ValidationException("Triggered product discount is missing trigger product configuration");
        }

        Integer configuredTriggerQuantity = extractPositiveInteger(
                benefit.getRawRuleConfig(),
                "triggerQuantity",
                "requiredTriggerQuantity",
                "minimumTriggerQuantity"
        );
        int requiredQuantity = configuredTriggerQuantity != null ? configuredTriggerQuantity : 1;

        int matchedQuantity = items.stream()
                .filter(item -> matchesTrigger(item, triggerProductId, triggerProductName))
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return matchedQuantity >= requiredQuantity;
    }

    private boolean matchesTrigger(OrderItem item, String triggerProductId, String triggerProductName) {
        if (triggerProductId != null && !triggerProductId.isBlank()) {
            return item.getProductId().getValue().toString().equals(triggerProductId);
        }

        return triggerProductName != null
                && !triggerProductName.isBlank()
                && item.getProductName() != null
                && item.getProductName().equalsIgnoreCase(triggerProductName.trim());
    }

    private boolean matchesScope(OrderItem item, CouponScope scope) {
        return switch (scope.getType()) {
            case ENTIRE_ORDER -> true;
            case SPECIFIC_PRODUCT, MANUAL_SELECTION -> matchesProductSelection(item, scope);
            case CATEGORY -> item.getProductCategory() != null && scope.getCategories().contains(item.getProductCategory());
            case SUBCATEGORY -> item.getProductSubcategory() != null && scope.getSubcategories().contains(item.getProductSubcategory());
            case BEER_TYPE -> item.getBeerType() != null && scope.getBeerTypes().contains(item.getBeerType());
            default -> false;
        };
    }

    private boolean matchesProductSelection(OrderItem item, CouponScope scope) {
        Set<String> productIds = scope.getProductIds();
        Set<String> variantIds = scope.getVariantIds();

        if (productIds.contains(item.getProductId().getValue().toString())) {
            return true;
        }

        return item.getProductVariantId() != null
                && variantIds.contains(item.getProductVariantId().getValue().toString());
    }

    private record ProductPromotionOutcome(List<OrderItem> items, Money discountAmount) {
    }
}
