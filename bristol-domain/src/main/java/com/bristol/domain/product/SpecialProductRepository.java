package com.bristol.domain.product;

import com.bristol.domain.catalog.SpecialTypeId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for SpecialProduct aggregate.
 */
public interface SpecialProductRepository {

    SpecialProduct save(SpecialProduct product);

    Optional<SpecialProduct> findById(ProductId id);

    List<SpecialProduct> findAll();

    List<SpecialProduct> findBySpecialType(SpecialTypeId specialTypeId);

    List<SpecialProduct> findRequiringQuote();

    List<SpecialProduct> findNotRequiringQuote();

    List<SpecialProduct> findFeatured();

    List<SpecialProduct> findInStock();

    List<SpecialProduct> findLowStock();

    List<SpecialProduct> searchByName(String name);

    void delete(ProductId id);
}
