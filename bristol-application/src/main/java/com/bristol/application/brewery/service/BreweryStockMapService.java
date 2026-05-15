package com.bristol.application.brewery.service;

import com.bristol.domain.brewery.BreweryInventory;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caches the brewery inventory map (beerStyleId → totalCans) to avoid N+1 queries
 * when the UnifiedProductMapper resolves beer product stock.
 * Evicted by any brewery inventory write operation.
 */
@Service
@RequiredArgsConstructor
public class BreweryStockMapService {

    private final BreweryInventoryRepository breweryInventoryRepository;

    @Cacheable("breweryStockMap")
    public Map<String, Integer> getStockMap() {
        return breweryInventoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        inv -> inv.getBeerStyleId().asString(),
                        BreweryInventory::getTotalCans
                ));
    }

    @CacheEvict(value = "breweryStockMap", allEntries = true)
    public void evict() {}
}
