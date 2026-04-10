package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for coupons table.
 */
@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false)
    private CouponAppliesToEnum appliesTo;

    @Column(name = "selected_items", columnDefinition = "TEXT")
    private String selectedItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponMethodEnum method;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private CouponDiscountTypeEnum discountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private CouponValueTypeEnum valueType;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private CouponScheduleTypeEnum scheduleType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatusEnum status;

    @Column(nullable = false)
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_requirement_type")
    private MinimumRequirementTypeEnum minimumRequirementType;

    @Column(name = "minimum_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;

    @Column(name = "minimum_item_quantity")
    private Integer minimumItemQuantity;

    @Column(name = "usage_limit_total")
    private Integer usageLimitTotal;

    @Column(name = "usage_limit_per_customer")
    private Integer usageLimitPerCustomer;

    @Column(name = "times_used", nullable = false)
    private Integer timesUsed;

    @Column(name = "is_customer_specific", nullable = false)
    private Boolean isCustomerSpecific;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_product_category")
    private ApplicableProductCategoryEnum applicableProductCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_product_subcategory")
    private ApplicableProductSubcategoryEnum applicableProductSubcategory;

    @Column(name = "combine_with_product_discounts", nullable = false)
    private Boolean combineWithProductDiscounts;

    @Column(name = "combine_with_order_discounts", nullable = false)
    private Boolean combineWithOrderDiscounts;

    @Column(name = "combine_with_shipping_discounts", nullable = false)
    private Boolean combineWithShippingDiscounts;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private CouponTriggerTypeEnum triggerType;

    @Column(name = "trigger_product_id", length = 255)
    private String triggerProductId;

    @Column(name = "trigger_product_name", length = 255)
    private String triggerProductName;

    @Column(name = "applies_to_future_orders", nullable = false)
    private Boolean appliesToFutureOrders;

    @Column(name = "specific_customers", columnDefinition = "TEXT")
    private String specificCustomers;

    @Column(name = "rule_config", columnDefinition = "TEXT")
    private String ruleConfig;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (timesUsed == null) {
            timesUsed = 0;
        }
        if (priority == null) {
            priority = 0;
        }
        if (isCustomerSpecific == null) {
            isCustomerSpecific = false;
        }
        if (selectedItems == null) {
            selectedItems = "[]";
        }
        if (appliesTo == null) {
            appliesTo = CouponAppliesToEnum.ENTIRE_ORDER;
        }
        if (combineWithProductDiscounts == null) {
            combineWithProductDiscounts = false;
        }
        if (combineWithOrderDiscounts == null) {
            combineWithOrderDiscounts = false;
        }
        if (combineWithShippingDiscounts == null) {
            combineWithShippingDiscounts = false;
        }
        if (triggerType == null) {
            triggerType = CouponTriggerTypeEnum.NONE;
        }
        if (appliesToFutureOrders == null) {
            appliesToFutureOrders = false;
        }
        if (specificCustomers == null) {
            specificCustomers = "[]";
        }
        if (ruleConfig == null) {
            ruleConfig = "{}";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum CouponMethodEnum {
        CODE, AUTOMATIC
    }

    public enum CouponAppliesToEnum {
        ENTIRE_ORDER, SPECIFIC_PRODUCTS, COLLECTIONS
    }

    public enum CouponDiscountTypeEnum {
        PRODUCT, ORDER, SHIPPING
    }

    public enum CouponValueTypeEnum {
        PERCENTAGE, FIXED
    }

    public enum CouponScheduleTypeEnum {
        ALWAYS, SCHEDULED, UNSCHEDULED
    }

    public enum CouponStatusEnum {
        DRAFT, ACTIVE, INACTIVE, EXPIRED
    }

    public enum MinimumRequirementTypeEnum {
        NONE, PURCHASE_AMOUNT, ITEM_QUANTITY, BOTH
    }

    public enum ApplicableProductCategoryEnum {
        ALL, BEER, MERCH, FOOD
    }

    public enum ApplicableProductSubcategoryEnum {
        ALL, BOTTLES, CANS, DRAFT, CLOTHING, ACCESSORIES, GLASSWARE, SNACKS, MEALS
    }

    public enum CouponTriggerTypeEnum {
        NONE, PRODUCT_PURCHASE, BUY_X_GET_Y
    }
}
