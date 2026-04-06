package com.bristol.domain.product;

/**
 * Product subcategory enumeration.
 * Matches the product_subcategory ENUM in the database schema.
 */
public enum ProductSubcategory {
    // Beer subcategories
    TWENTY_FOUR_PACK("24-pack"),
    SIX_PACK("six-pack"),
    CAN("can"),
    KEG("keg"),
    GROWLER("growler"),

    // Merch subcategories
    REMERA("remera"),
    BUZO("buzo"),
    GORRA("gorra"),
    VASO("vaso"),

    // Special subcategories
    PLOTEO("ploteo"),
    EVENTO("evento"),
    OTRO("otro");

    private final String value;

    ProductSubcategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
