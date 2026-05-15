package com.bristol.application.brewery.service;

import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BreweryInventoryService {

    private final BreweryInventoryRepository inventoryRepository;
    private final TimeProvider timeProvider;

    /**
     * Deducts cans from the brewery inventory when a beer product is sold.
     * cansToDeduct = product.cansPerUnit * quantitySold
     */
    @Transactional
    public void deductCansForSale(BeerProduct product, int quantitySold) {
        BeerStyleId beerStyleId = product.getBeerStyleId();
        if (beerStyleId == null) {
            return;
        }

        int cansPerUnit = product.getCansPerUnit() != null ? product.getCansPerUnit() : 1;
        int totalCansToDeduct = cansPerUnit * quantitySold;

        BreweryInventory inventory = inventoryRepository.findByBeerStyleId(beerStyleId)
                .orElseThrow(() -> new ValidationException(
                        "No brewery inventory found for style of product: " + product.getName() +
                        ". Please set up the inventory for this beer style before selling."));

        if (!inventory.hasEnoughCans(totalCansToDeduct)) {
            throw new ValidationException(
                    "Insufficient can inventory for: " + product.getName() +
                    ". Available: " + inventory.getTotalCans() +
                    ", Required: " + totalCansToDeduct +
                    " (" + quantitySold + " units × " + cansPerUnit + " cans/unit)");
        }

        inventoryRepository.save(inventory.deductCans(totalCansToDeduct, timeProvider.now()));
    }

    /**
     * Restores cans to the brewery inventory when a beer order is cancelled.
     */
    @Transactional
    public void restoreCansForCancellation(BeerProduct product, int quantityRestored) {
        BeerStyleId beerStyleId = product.getBeerStyleId();
        if (beerStyleId == null) {
            return;
        }

        int cansPerUnit = product.getCansPerUnit() != null ? product.getCansPerUnit() : 1;
        int totalCansToRestore = cansPerUnit * quantityRestored;

        inventoryRepository.findByBeerStyleId(beerStyleId).ifPresent(inventory ->
                inventoryRepository.save(inventory.addCans(totalCansToRestore, timeProvider.now()))
        );
    }
}
