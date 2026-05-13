package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartAppliedPromotionDto;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponBenefit;
import com.bristol.domain.coupon.CouponBenefitType;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponRedemptionRepository;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponValueType;
import com.bristol.domain.coupon.PromotionApplication;
import com.bristol.domain.coupon.PromotionEngine;
import com.bristol.domain.coupon.PromotionEvaluationResult;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reuses the promotion engine to expose cart pricing with automatic promotions already applied.
 */
@Service
@RequiredArgsConstructor
public class CartPricingPreviewService {

    private static final ShippingAddress PREVIEW_SHIPPING_ADDRESS = ShippingAddress.of(
            "Cart preview",
            null,
            "CABA",
            "Buenos Aires",
            null,
            null
    );

    private final UnifiedProductService unifiedProductService;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final TimeProvider timeProvider;
    private final UserRepository userRepository;

    public CartPricingPreview preview(ShoppingCart cart) {
        return preview(cart, null);
    }

    public CartPricingPreview preview(ShoppingCart cart, String couponCode) {
        if (cart == null || cart.getItems().isEmpty()) {
            return CartPricingPreview.empty();
        }

        Instant now = timeProvider.now();
        List<OrderItem> orderItems = buildOrderItems(cart);
        Order order = Order.create(
                cart.getUserId(),
                PREVIEW_SHIPPING_ADDRESS,
                orderItems,
                Money.zero(),
                null,
                now
        );

        String userEmail = userRepository.findById(cart.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);

        PromotionEngine promotionEngine = new PromotionEngine(timeProvider, couponRedemptionRepository);
        Optional<Coupon> requestedCoupon = optionalCouponCode(couponCode).map(this::loadRequestedCoupon);

        requestedCoupon.ifPresent(coupon -> promotionEngine.validateCoupon(order, coupon, userEmail));

        List<Coupon> candidates = collectCandidates(requestedCoupon);
        PromotionEvaluationResult evaluation = promotionEngine.evaluate(order, candidates, userEmail);

        ensureRequestedPromotionSurvived(evaluation, requestedCoupon);

        Order repricedOrder = order.applyPromotions(
                evaluation.repricedItems(),
                evaluation.getOrderPromotion().map(application -> application.coupon().getId()).orElse(null),
                evaluation.getOrderPromotion().map(PromotionApplication::discountAmount).orElse(Money.zero()),
                evaluation.getShippingPromotion().map(application -> application.coupon().getId()).orElse(null),
                evaluation.getShippingPromotion().map(PromotionApplication::discountAmount).orElse(Money.zero()),
                now
        );

        Map<String, OrderItem> repricedItemsByKey = repricedOrder.getItems().stream()
                .collect(Collectors.toMap(this::buildItemKey, item -> item, (left, right) -> left, LinkedHashMap::new));

        return new CartPricingPreview(
                repricedItemsByKey,
                buildProductPromotionsByCouponId(evaluation.getProductPromotions()),
                evaluation.getOrderPromotion().map(PromotionApplication::coupon).map(this::toPromotionDto).orElse(null),
                order.getSubtotal().getAmount(),
                calculateItemDiscountAmount(repricedOrder.getItems()).getAmount(),
                repricedOrder.getSubtotal().getAmount(),
                repricedOrder.getOrderDiscountAmount().getAmount(),
                repricedOrder.getTotal().getAmount()
        );
    }

    private List<Coupon> collectCandidates(Optional<Coupon> requestedCoupon) {
        LinkedHashMap<String, Coupon> candidates = new LinkedHashMap<>();
        requestedCoupon.ifPresent(coupon -> candidates.put(coupon.getId().getValue().toString(), coupon));

        couponRepository.findAutomatic().stream()
                .filter(Coupon::isActive)
                .sorted(Comparator.comparingInt(Coupon::getPriority)
                        .reversed()
                        .thenComparing(Coupon::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(coupon -> candidates.putIfAbsent(coupon.getId().getValue().toString(), coupon));

        return new ArrayList<>(candidates.values());
    }

    private Coupon loadRequestedCoupon(String couponCode) {
        return couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ValidationException("Invalid coupon code"));
    }

    private void ensureRequestedPromotionSurvived(
            PromotionEvaluationResult evaluation,
            Optional<Coupon> requestedCoupon
    ) {
        if (requestedCoupon.isPresent() && !evaluation.containsCoupon(requestedCoupon.orElseThrow().getId())) {
            CouponDiscountType discountType = requestedCoupon.orElseThrow().getDiscountType();
            if (discountType == CouponDiscountType.ORDER) {
                throw new ValidationException("This order coupon cannot be combined with the promotions already applied to the order");
            }
            if (discountType == CouponDiscountType.SHIPPING) {
                throw new ValidationException("This shipping coupon cannot be combined with the promotions already applied to the order");
            }
            throw new ValidationException("This coupon cannot be combined with the promotions already applied to the order");
        }
    }

    private Optional<String> optionalCouponCode(String couponCode) {
        return Optional.ofNullable(couponCode)
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    private List<OrderItem> buildOrderItems(ShoppingCart cart) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            BaseProduct product = unifiedProductService.findById(item.getProductId()).orElse(null);
            orderItems.add(OrderItem.create(
                    com.bristol.domain.order.OrderId.generate(),
                    item.getProductId(),
                    item.getProductVariantId(),
                    item.getProductName(),
                    item.getProductType(),
                    item.getBeerType(),
                    product != null ? product.getCategory() : null,
                    product != null ? product.getSubcategory() : null,
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
        }
        return orderItems;
    }

    private String buildItemKey(CartItem item) {
        return buildItemKey(item.getProductId().getValue().toString(),
                item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null);
    }

    private String buildItemKey(OrderItem item) {
        return buildItemKey(item.getProductId().getValue().toString(),
                item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null);
    }

    private String buildItemKey(String productId, String variantId) {
        return productId + "::" + (variantId != null ? variantId : "");
    }

    private Money calculateItemDiscountAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getItemDiscountAmount)
                .reduce(Money.zero(), Money::add);
    }

    private Map<String, CartAppliedPromotionDto> buildProductPromotionsByCouponId(List<PromotionApplication> promotions) {
        Map<String, CartAppliedPromotionDto> promotionsByCouponId = new LinkedHashMap<>();
        for (PromotionApplication promotion : promotions) {
            CartAppliedPromotionDto dto = toPromotionDto(promotion.coupon());
            promotionsByCouponId.put(dto.getId(), dto);
        }
        return promotionsByCouponId;
    }

    private CartAppliedPromotionDto toPromotionDto(Coupon coupon) {
        CouponBenefit benefit = coupon.getBenefit();
        return CartAppliedPromotionDto.builder()
                .id(coupon.getId().getValue().toString())
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .badgeText(buildBadgeText(benefit))
                .detailsText(buildDetailsText(coupon, benefit))
                .code(coupon.getCode())
                .method(coupon.getMethod())
                .discountType(coupon.getDiscountType())
                .build();
    }

    private String buildBadgeText(CouponBenefit benefit) {
        return switch (benefit.getType()) {
            case PRODUCT_PERCENTAGE, TRIGGERED_PRODUCT_DISCOUNT, PERCENTAGE_ON_QUANTITY -> formatPercentage(benefit.getValue()) + " OFF";
            case PRODUCT_FIXED_AMOUNT -> formatMoney(benefit.getValue()) + " OFF";
            case BUY_X_GET_Y -> buildBuyXGetYText(benefit);
            case BUY_X_FOR_Y -> buildBuyXForYText(benefit);
            case QUANTITY_RULE -> benefit.getValueType() == CouponValueType.PERCENTAGE
                    ? formatPercentage(benefit.getValue()) + " OFF"
                    : formatMoney(benefit.getValue()) + " OFF";
            default -> "PROMO";
        };
    }

    private String buildDetailsText(Coupon coupon, CouponBenefit benefit) {
        if (coupon.getDiscountType() == CouponDiscountType.ORDER) {
            return coupon.requiresCode()
                    ? "Cupón aplicado al subtotal del pedido"
                    : "Promoción automática sobre el subtotal del pedido";
        }

        if (benefit.getType() == CouponBenefitType.BUY_X_GET_Y) {
            return buildBuyXGetYDetails(benefit);
        }

        if (benefit.getType() == CouponBenefitType.BUY_X_FOR_Y) {
            return buildBuyXForYDetails(benefit);
        }

        return coupon.requiresCode()
                ? "Cupón aplicado al producto"
                : "Promoción automática aplicada al producto";
    }

    private String buildBuyXGetYText(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer freeQuantity = benefit.getFreeQuantity();
        if (buyQuantity == null || freeQuantity == null) {
            return "PROMO";
        }
        return (buyQuantity + freeQuantity) + "x" + buyQuantity;
    }

    private String buildBuyXForYText(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer payQuantity = benefit.getPayQuantity();
        if (buyQuantity == null || payQuantity == null) {
            return "PROMO";
        }
        return buyQuantity + "x" + payQuantity;
    }

    private String buildBuyXGetYDetails(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer freeQuantity = benefit.getFreeQuantity();
        if (buyQuantity == null || freeQuantity == null) {
            return "Promoción aplicada al subtotal del producto";
        }
        return "Llevando " + (buyQuantity + freeQuantity) + " pagas " + buyQuantity;
    }

    private String buildBuyXForYDetails(CouponBenefit benefit) {
        Integer buyQuantity = benefit.getBuyQuantity();
        Integer payQuantity = benefit.getPayQuantity();
        if (buyQuantity == null || payQuantity == null) {
            return "Promoción aplicada al subtotal del producto";
        }
        return "Llevando " + buyQuantity + " pagas " + payQuantity;
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

    public record CartPricingPreview(
            Map<String, OrderItem> repricedItemsByKey,
            Map<String, CartAppliedPromotionDto> appliedProductPromotionsByCouponId,
            CartAppliedPromotionDto appliedOrderPromotion,
            BigDecimal originalSubtotal,
            BigDecimal productDiscountAmount,
            BigDecimal subtotal,
            BigDecimal orderDiscountAmount,
            BigDecimal total
    ) {
        static CartPricingPreview empty() {
            return new CartPricingPreview(
                    Map.of(),
                    Map.of(),
                    null,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        public Optional<OrderItem> findItem(String productId, String productVariantId) {
            return Optional.ofNullable(repricedItemsByKey.get(productId + "::" + (productVariantId != null ? productVariantId : "")));
        }

        public Optional<CartAppliedPromotionDto> findAppliedProductPromotion(String couponId) {
            if (couponId == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(appliedProductPromotionsByCouponId.get(couponId));
        }

        public Optional<CartAppliedPromotionDto> appliedProductPromotion() {
            return appliedProductPromotionsByCouponId.values().stream().findFirst();
        }
    }
}
