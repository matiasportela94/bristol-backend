package com.bristol.domain.coupon;

import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductSubcategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal normalized view of coupon targeting.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponScope {

    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("\"productId\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PRODUCT_IDS_BLOCK_PATTERN = Pattern.compile("\"productIds\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern VARIANT_ID_PATTERN = Pattern.compile("\"variantId\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern VARIANT_IDS_BLOCK_PATTERN = Pattern.compile("\"variantIds\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern BEER_TYPE_PATTERN = Pattern.compile("\"beerType\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern BEER_TYPES_BLOCK_PATTERN = Pattern.compile("\"beerTypes\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("\"category\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern CATEGORIES_BLOCK_PATTERN = Pattern.compile("\"categories\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern SUBCATEGORY_PATTERN = Pattern.compile("\"subcategory\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern SUBCATEGORIES_BLOCK_PATTERN = Pattern.compile("\"subcategories\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]+)\"");

    private final CouponScopeType type;
    private final CouponAppliesTo appliesTo;
    private final String rawSelection;

    public static CouponScope from(CouponAppliesTo appliesTo, String selectedItems) {
        String normalizedSelection = normalizePayload(selectedItems, "[]");
        CouponAppliesTo resolvedAppliesTo = appliesTo != null ? appliesTo : CouponAppliesTo.ENTIRE_ORDER;

        if (resolvedAppliesTo == CouponAppliesTo.ENTIRE_ORDER) {
            return new CouponScope(CouponScopeType.ENTIRE_ORDER, resolvedAppliesTo, normalizedSelection);
        }

        if (resolvedAppliesTo == CouponAppliesTo.COLLECTIONS) {
            return new CouponScope(CouponScopeType.COLLECTION, resolvedAppliesTo, normalizedSelection);
        }

        if (containsAnyIgnoreCase(normalizedSelection, "\"productId\"", "\"productIds\"", "\"variantId\"", "\"variantIds\"")) {
            return new CouponScope(CouponScopeType.SPECIFIC_PRODUCT, resolvedAppliesTo, normalizedSelection);
        }
        if (containsAnyIgnoreCase(normalizedSelection, "\"beerType\"", "\"beerTypes\"")) {
            return new CouponScope(CouponScopeType.BEER_TYPE, resolvedAppliesTo, normalizedSelection);
        }
        if (containsAnyIgnoreCase(normalizedSelection, "\"subcategory\"", "\"subcategories\"")) {
            return new CouponScope(CouponScopeType.SUBCATEGORY, resolvedAppliesTo, normalizedSelection);
        }
        if (containsAnyIgnoreCase(normalizedSelection, "\"category\"", "\"categories\"")) {
            return new CouponScope(CouponScopeType.CATEGORY, resolvedAppliesTo, normalizedSelection);
        }
        if (!isEmptySelection(normalizedSelection)) {
            return new CouponScope(CouponScopeType.MANUAL_SELECTION, resolvedAppliesTo, normalizedSelection);
        }

        return new CouponScope(CouponScopeType.MANUAL_SELECTION, resolvedAppliesTo, normalizedSelection);
    }

    public boolean isEntireOrder() {
        return type == CouponScopeType.ENTIRE_ORDER;
    }

    public boolean hasStructuredSelection() {
        return !isEmptySelection(rawSelection);
    }

    public Set<String> getProductIds() {
        Set<String> values = new LinkedHashSet<>();
        extractSingleValues(PRODUCT_ID_PATTERN, values);
        extractArrayValues(PRODUCT_IDS_BLOCK_PATTERN, values);
        return values;
    }

    public Set<String> getVariantIds() {
        Set<String> values = new LinkedHashSet<>();
        extractSingleValues(VARIANT_ID_PATTERN, values);
        extractArrayValues(VARIANT_IDS_BLOCK_PATTERN, values);
        return values;
    }

    public Set<BeerType> getBeerTypes() {
        Set<String> rawValues = new LinkedHashSet<>();
        extractSingleValues(BEER_TYPE_PATTERN, rawValues);
        extractArrayValues(BEER_TYPES_BLOCK_PATTERN, rawValues);

        Set<BeerType> beerTypes = new LinkedHashSet<>();
        for (String rawValue : rawValues) {
            tryAddEnum(rawValue, BeerType.class, beerTypes);
        }
        return beerTypes;
    }

    public Set<ProductCategory> getCategories() {
        Set<String> rawValues = new LinkedHashSet<>();
        extractSingleValues(CATEGORY_PATTERN, rawValues);
        extractArrayValues(CATEGORIES_BLOCK_PATTERN, rawValues);

        Set<ProductCategory> categories = new LinkedHashSet<>();
        for (String rawValue : rawValues) {
            tryAddEnum(rawValue, ProductCategory.class, categories);
        }
        return categories;
    }

    public Set<ProductSubcategory> getSubcategories() {
        Set<String> rawValues = new LinkedHashSet<>();
        extractSingleValues(SUBCATEGORY_PATTERN, rawValues);
        extractArrayValues(SUBCATEGORIES_BLOCK_PATTERN, rawValues);

        Set<ProductSubcategory> subcategories = new LinkedHashSet<>();
        for (String rawValue : rawValues) {
            tryAddEnum(rawValue, ProductSubcategory.class, subcategories);
        }
        return subcategories;
    }

    private <E extends Enum<E>> void tryAddEnum(String rawValue, Class<E> enumType, Set<E> target) {
        try {
            target.add(Enum.valueOf(enumType, rawValue.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignored) {
            // Ignore unknown values until the admin payload is normalized.
        }
    }

    private static boolean isEmptySelection(String payload) {
        return "[]".equals(payload) || "{}".equals(payload);
    }

    private static String normalizePayload(String payload, String fallback) {
        return payload != null && !payload.isBlank() ? payload : fallback;
    }

    private static boolean containsAnyIgnoreCase(String value, String... candidates) {
        String normalized = value.toLowerCase();
        for (String candidate : candidates) {
            if (normalized.contains(candidate.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void extractSingleValues(Pattern pattern, Set<String> target) {
        Matcher matcher = pattern.matcher(rawSelection);
        while (matcher.find()) {
            target.add(matcher.group(1));
        }
    }

    private void extractArrayValues(Pattern blockPattern, Set<String> target) {
        Matcher blockMatcher = blockPattern.matcher(rawSelection);
        while (blockMatcher.find()) {
            Matcher stringMatcher = STRING_PATTERN.matcher(blockMatcher.group(1));
            while (stringMatcher.find()) {
                target.add(stringMatcher.group(1));
            }
        }
    }
}
