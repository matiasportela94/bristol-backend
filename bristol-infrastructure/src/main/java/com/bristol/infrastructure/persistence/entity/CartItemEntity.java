package com.bristol.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "cart_id", nullable = false, columnDefinition = "UUID")
    private UUID cartId;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "product_variant_id", columnDefinition = "UUID")
    private UUID productVariantId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 32)
    private ProductTypeEnum productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "beer_type", length = 32)
    private BeerTypeEnum beerType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    public enum ProductTypeEnum {
        BEER, MERCH, SPECIAL
    }

    public enum BeerTypeEnum {
        IPA, LAGER, APA, STOUT, PORTER, PILSNER, SOUR, WHEAT, BLONDE, AMBER, GOLDEN, PALE_ALE, OTRO
    }
}
