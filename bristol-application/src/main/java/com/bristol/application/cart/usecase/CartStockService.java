package com.bristol.application.cart.usecase;

import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartStockService {

    private final BreweryInventoryRepository breweryInventoryRepository;

    /**
     * Returns the effective available stock for a product.
     * For BeerProduct: reads from brewery_inventory and converts cans → units (cans / cansPerUnit).
     * For everything else: reads from variant stock or product stock.
     */
    public int resolveAvailableStock(BaseProduct product, Optional<ProductVariant> variant) {
        if (product instanceof BeerProduct beerProduct && variant.isEmpty()) {
            int cansPerUnit = beerProduct.getCansPerUnit() != null && beerProduct.getCansPerUnit() > 0
                    ? beerProduct.getCansPerUnit() : 1;
            return breweryInventoryRepository
                    .findByBeerStyleId(beerProduct.getBeerStyleId())
                    .map(inv -> inv.getTotalCans() / cansPerUnit)
                    .orElse(0);
        }
        return variant.map(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .orElseGet(() -> product.getStockQuantity() != null ? product.getStockQuantity() : 0);
    }
}
