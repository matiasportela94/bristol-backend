package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA Entity for order_items table.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_id", nullable = false, columnDefinition = "UUID")
    private UUID orderId;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "product_variant_id", columnDefinition = "UUID")
    private UUID productVariantId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductTypeEnum productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "beer_type")
    private BeerTypeEnum beerType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_per_unit", precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerUnit;

    @Column(name = "item_discount_coupon_id", columnDefinition = "UUID")
    private UUID itemDiscountCouponId;

    @Column(name = "item_discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal itemDiscountAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (itemDiscountAmount == null) {
            itemDiscountAmount = BigDecimal.ZERO;
        }
    }

    public enum ProductTypeEnum {
        BEER, MERCH, SPECIAL
    }

    public enum BeerTypeEnum {
        IPA, LAGER, APA, STOUT, PORTER, PILSNER, SOUR, WHEAT, BLONDE, AMBER, GOLDEN, PALE_ALE, OTRO
    }
}
