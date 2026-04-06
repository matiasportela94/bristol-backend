package com.bristol.domain.product;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Product aggregate.
 */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(ProductId id);

    List<Product> findAll();

    List<Product> findByCategory(ProductCategory category);

    List<Product> findFeatured();

    List<Product> findInStock();

    List<Product> findLowStock();

    List<Product> findByBeerType(BeerType beerType);

    List<Product> searchByName(String name);

    void delete(ProductId id);
}
