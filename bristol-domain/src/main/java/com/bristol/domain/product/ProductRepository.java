package com.bristol.domain.product;

import com.bristol.domain.shared.Page;

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

    /**
     * Find all products with pagination.
     */
    Page<Product> findAllPaginated(int pageNumber, int pageSize);

    /**
     * Find products by category with pagination.
     */
    Page<Product> findByCategoryPaginated(ProductCategory category, int pageNumber, int pageSize);

    /**
     * Find featured products with pagination.
     */
    Page<Product> findFeaturedPaginated(int pageNumber, int pageSize);

    /**
     * Search products by name or description with pagination.
     * @param query Search query
     * @param pageNumber Page number (0-indexed)
     * @param pageSize Page size
     * @return Page of products matching the search query
     */
    Page<Product> searchPaginated(String query, int pageNumber, int pageSize);

    void delete(ProductId id);
}
