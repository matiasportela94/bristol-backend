package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.BeerCostAnalysisDto;
import com.bristol.application.stats.dto.BeerStyleCostDto;
import com.bristol.domain.brewery.BreweryBatch;
import com.bristol.domain.brewery.BreweryBatchRepository;
import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.BeerProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBeerCostAnalysisUseCase {

    private static final int SCALE = 4;

    private final BreweryBatchRepository batchRepository;
    private final BreweryInventoryRepository inventoryRepository;
    private final BeerProductRepository beerProductRepository;
    private final BeerStyleRepository beerStyleRepository;

    @Transactional(readOnly = true)
    public BeerCostAnalysisDto execute() {
        Map<String, BeerStyle> stylesById = beerStyleRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getId().asString(), s -> s));

        Map<String, BreweryInventory> inventoryByStyle = inventoryRepository.findAll().stream()
                .collect(Collectors.toMap(i -> i.getBeerStyleId().asString(), i -> i));

        Map<String, List<BreweryBatch>> batchesByStyle = batchRepository.findAll().stream()
                .collect(Collectors.groupingBy(b -> b.getBeerStyleId().asString()));

        Map<String, List<BeerProduct>> productsByStyle = beerProductRepository.findAll().stream()
                .filter(p -> !p.isDeleted() && p.getBeerStyleId() != null)
                .collect(Collectors.groupingBy(p -> p.getBeerStyleId().asString()));

        List<BeerStyleCostDto> styles = stylesById.entrySet().stream()
                .map(entry -> buildStyleCost(
                        entry.getKey(),
                        entry.getValue(),
                        inventoryByStyle.get(entry.getKey()),
                        batchesByStyle.getOrDefault(entry.getKey(), List.of()),
                        productsByStyle.getOrDefault(entry.getKey(), List.of())
                ))
                .sorted(Comparator.comparing(BeerStyleCostDto::getStyleName))
                .toList();

        BigDecimal totalPpp = styles.stream()
                .map(s -> s.getInventoryValueAtPpp() != null ? s.getInventoryValueAtPpp() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalFifo = styles.stream()
                .map(s -> s.getInventoryValueAtFifo() != null ? s.getInventoryValueAtFifo() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return BeerCostAnalysisDto.builder()
                .styles(styles)
                .totalInventoryValueAtPpp(totalPpp)
                .totalInventoryValueAtFifo(totalFifo)
                .build();
    }

    private BeerStyleCostDto buildStyleCost(
            String styleId,
            BeerStyle style,
            BreweryInventory inventory,
            List<BreweryBatch> batches,
            List<BeerProduct> products
    ) {
        int totalCans = inventory != null ? inventory.getTotalCans() : 0;

        List<BreweryBatch> batchesWithCost = batches.stream()
                .filter(b -> b.getCostPerCan() != null)
                .toList();

        BigDecimal pppCostPerCan = computePpp(batchesWithCost);
        BigDecimal fifoCostPerCan = computeFifo(batchesWithCost);

        BigDecimal lowestPricePerCan = products.stream()
                .filter(p -> p.getBasePrice() != null && p.getCansPerUnit() != null && p.getCansPerUnit() > 0)
                .map(p -> p.getBasePrice().getAmount()
                        .divide(BigDecimal.valueOf(p.getCansPerUnit()), SCALE, RoundingMode.HALF_UP))
                .min(BigDecimal::compareTo)
                .orElse(null);

        BigDecimal pppMarginPerCan = margin(lowestPricePerCan, pppCostPerCan);
        BigDecimal pppMarginPercent = marginPercent(lowestPricePerCan, pppMarginPerCan);
        BigDecimal fifoMarginPerCan = margin(lowestPricePerCan, fifoCostPerCan);
        BigDecimal fifoMarginPercent = marginPercent(lowestPricePerCan, fifoMarginPerCan);

        BigDecimal inventoryValueAtPpp = pppCostPerCan != null
                ? pppCostPerCan.multiply(BigDecimal.valueOf(totalCans)).setScale(2, RoundingMode.HALF_UP)
                : null;
        BigDecimal inventoryValueAtFifo = fifoCostPerCan != null
                ? fifoCostPerCan.multiply(BigDecimal.valueOf(totalCans)).setScale(2, RoundingMode.HALF_UP)
                : null;

        return BeerStyleCostDto.builder()
                .styleId(styleId)
                .styleName(style.getName())
                .styleCode(style.getCode())
                .totalCans(totalCans)
                .lowestPricePerCan(lowestPricePerCan != null ? lowestPricePerCan.setScale(2, RoundingMode.HALF_UP) : null)
                .pppCostPerCan(pppCostPerCan != null ? pppCostPerCan.setScale(2, RoundingMode.HALF_UP) : null)
                .fifoCostPerCan(fifoCostPerCan != null ? fifoCostPerCan.setScale(2, RoundingMode.HALF_UP) : null)
                .pppMarginPerCan(pppMarginPerCan != null ? pppMarginPerCan.setScale(2, RoundingMode.HALF_UP) : null)
                .pppMarginPercent(pppMarginPercent)
                .fifoMarginPerCan(fifoMarginPerCan != null ? fifoMarginPerCan.setScale(2, RoundingMode.HALF_UP) : null)
                .fifoMarginPercent(fifoMarginPercent)
                .inventoryValueAtPpp(inventoryValueAtPpp)
                .inventoryValueAtFifo(inventoryValueAtFifo)
                .batchCount(batches.size())
                .batchesWithCostCount(batchesWithCost.size())
                .build();
    }

    /** Weighted-average cost: Σ(cansProduced × costPerCan) / Σ(cansProduced) */
    private BigDecimal computePpp(List<BreweryBatch> batchesWithCost) {
        if (batchesWithCost.isEmpty()) return null;

        BigDecimal totalCost = batchesWithCost.stream()
                .map(b -> b.getCostPerCan().multiply(BigDecimal.valueOf(b.getCansProduced())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalCans = batchesWithCost.stream()
                .mapToInt(BreweryBatch::getCansProduced)
                .sum();

        if (totalCans == 0) return null;
        return totalCost.divide(BigDecimal.valueOf(totalCans), SCALE, RoundingMode.HALF_UP);
    }

    /** Cost of the oldest batch that has cost data */
    private BigDecimal computeFifo(List<BreweryBatch> batchesWithCost) {
        return batchesWithCost.stream()
                .min(Comparator.comparing(BreweryBatch::getCreatedAt))
                .map(BreweryBatch::getCostPerCan)
                .orElse(null);
    }

    private BigDecimal margin(BigDecimal price, BigDecimal cost) {
        if (price == null || cost == null) return null;
        return price.subtract(cost);
    }

    private BigDecimal marginPercent(BigDecimal price, BigDecimal marginAmount) {
        if (price == null || marginAmount == null || price.compareTo(BigDecimal.ZERO) == 0) return null;
        return marginAmount
                .divide(price, SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
