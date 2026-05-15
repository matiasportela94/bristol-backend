package com.bristol.application.product.service;

import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.product.BeerProductRepository;
import com.bristol.domain.product.MerchProductRepository;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockSyncService {

    private final BeerProductRepository beerProductRepository;
    private final MerchProductRepository merchProductRepository;
    private final ProductVariantRepository productVariantRepository;
    private final TimeProvider timeProvider;

    /**
     * Called after a brewing batch is added.
     * Updates stock_quantity on every BeerProduct linked to that style.
     */
    @Transactional
    public void syncBeerStock(BeerStyleId styleId, int totalCans) {
        var now = timeProvider.now();
        beerProductRepository.findByBeerStyle(styleId).forEach(product -> {
            int cansPerUnit = product.getCansPerUnit() != null && product.getCansPerUnit() > 0
                    ? product.getCansPerUnit() : 1;
            beerProductRepository.save(product.updateStock(totalCans / cansPerUnit, now));
        });
    }

    /**
     * Called after any variant is created, updated or deleted.
     * Recalculates the sum of all variant stocks and updates the parent product.
     */
    @Transactional
    public void syncMerchStock(ProductId productId) {
        int total = productVariantRepository.findByProductId(productId).stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        var now = timeProvider.now();
        merchProductRepository.findById(productId).ifPresent(product ->
                merchProductRepository.save(product.updateStock(total, now))
        );
    }
}
