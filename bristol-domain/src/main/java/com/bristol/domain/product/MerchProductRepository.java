package com.bristol.domain.product;

import com.bristol.domain.catalog.MerchCategory;
import com.bristol.domain.catalog.MerchTypeId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for MerchProduct aggregate.
 */
public interface MerchProductRepository {

    MerchProduct save(MerchProduct product);

    Optional<MerchProduct> findById(ProductId id);

    List<MerchProduct> findAll();

    List<MerchProduct> findByMerchType(MerchTypeId merchTypeId);

    List<MerchProduct> findByCategory(MerchCategory category);

    List<MerchProduct> findFeatured();

    List<MerchProduct> findInStock();

    List<MerchProduct> findLowStock();

    List<MerchProduct> searchByName(String name);

    List<MerchProduct> findByBrand(String brand);

    void delete(ProductId id);
}
