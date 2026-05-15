package com.bristol.application.stats.usecase;

import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.application.stats.dto.CatalogStatsDto;
import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCatalogStatsUseCase {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 50;

    private final UnifiedProductService unifiedProductService;
    private final BeerProductRepository beerProductRepository;
    private final BreweryInventoryRepository breweryInventoryRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public CatalogStatsDto execute() {
        List<BaseProduct> allProducts = unifiedProductService.findAll().stream()
                .filter(p -> !p.isDeleted())
                .toList();

        // --- Beer: source of truth is brewery_inventory ---
        List<BreweryInventory> inventories = breweryInventoryRepository.findAll();

        long beerTotalCans = inventories.stream()
                .mapToLong(BreweryInventory::getTotalCans)
                .sum();

        // Group beer products by beerStyleId to find cheapest per-can price per style
        Map<String, List<BeerProduct>> productsByStyle = beerProductRepository.findAll().stream()
                .filter(p -> !p.isDeleted() && p.getBeerStyleId() != null)
                .collect(Collectors.groupingBy(p -> p.getBeerStyleId().asString()));

        BigDecimal beerValue = inventories.stream()
                .map(inv -> {
                    String styleId = inv.getBeerStyleId().asString();
                    List<BeerProduct> styleProducts = productsByStyle.getOrDefault(styleId, List.of());
                    if (styleProducts.isEmpty()) return BigDecimal.ZERO;

                    // Cheapest price per individual can across all products of this style
                    BigDecimal pricePerCan = styleProducts.stream()
                            .filter(p -> p.getBasePrice() != null
                                    && p.getCansPerUnit() != null
                                    && p.getCansPerUnit() > 0)
                            .map(p -> p.getBasePrice().getAmount()
                                    .divide(BigDecimal.valueOf(p.getCansPerUnit()), 4, RoundingMode.HALF_UP))
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    return pricePerCan.multiply(BigDecimal.valueOf(inv.getTotalCans()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int beerLowStock = (int) inventories.stream()
                .filter(inv -> inv.getTotalCans() > 0 && inv.getTotalCans() <= DEFAULT_LOW_STOCK_THRESHOLD)
                .count();

        // --- Non-beer (merch + special): read from product/variant stock ---
        long nonBeerUnits = 0;
        BigDecimal nonBeerValue = BigDecimal.ZERO;
        int nonBeerLowStock = 0;
        int nonBeerOutOfStockVariants = 0;

        for (BaseProduct p : allProducts) {
            if (p.isBeer()) continue;

            List<ProductVariant> variants = productVariantRepository.findByProductId(p.getId());
            int stock = variants.isEmpty()
                    ? (p.getStockQuantity() != null ? p.getStockQuantity() : 0)
                    : variants.stream()
                            .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                            .sum();

            nonBeerUnits += stock;

            if (p.getBasePrice() != null && stock > 0) {
                nonBeerValue = nonBeerValue.add(
                        p.getBasePrice().getAmount().multiply(BigDecimal.valueOf(stock))
                );
            }

            int threshold = p.getLowStockThreshold() != null ? p.getLowStockThreshold() : DEFAULT_LOW_STOCK_THRESHOLD;
            boolean isLowStock = variants.isEmpty()
                    ? (stock > 0 && stock <= threshold)
                    : variants.stream().anyMatch(v -> {
                          int vs = v.getStockQuantity() != null ? v.getStockQuantity() : 0;
                          return vs > 0 && vs <= threshold;
                      });
            if (isLowStock) {
                nonBeerLowStock++;
            }

            if (p.isMerch() && !variants.isEmpty() &&
                    variants.stream().anyMatch(v -> (v.getStockQuantity() == null || v.getStockQuantity() == 0))) {
                nonBeerOutOfStockVariants++;
            }
        }

        return CatalogStatsDto.builder()
                .totalProducts(allProducts.size())
                .totalUnitsInStock(beerTotalCans + nonBeerUnits)
                .totalValue(beerValue.add(nonBeerValue).setScale(2, RoundingMode.HALF_UP))
                .lowStockCount(beerLowStock + nonBeerLowStock)
                .outOfStockVariantsCount(nonBeerOutOfStockVariants)
                .build();
    }
}
