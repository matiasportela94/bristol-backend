package com.bristol.application.product.service;

import com.bristol.domain.product.*;
import com.bristol.domain.shared.Page;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Unified service to work with all product types polymorphically.
 * Delegates to specific repositories (Beer, Merch, Special) based on product type.
 */
@Service
@RequiredArgsConstructor
public class UnifiedProductService {

    private final BeerProductRepository beerProductRepository;
    private final MerchProductRepository merchProductRepository;
    private final SpecialProductRepository specialProductRepository;

    /**
     * Find product by ID (any type).
     */
    @Transactional(readOnly = true)
    public Optional<BaseProduct> findById(ProductId id) {
        // Try each repository
        Optional<BeerProduct> beer = beerProductRepository.findById(id);
        if (beer.isPresent()) {
            return Optional.of(beer.get());
        }

        Optional<MerchProduct> merch = merchProductRepository.findById(id);
        if (merch.isPresent()) {
            return Optional.of(merch.get());
        }

        Optional<SpecialProduct> special = specialProductRepository.findById(id);
        return special.map(sp -> sp);
    }

    /**
     * Find all products (all types).
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findAll() {
        List<BaseProduct> allProducts = new ArrayList<>();
        allProducts.addAll(beerProductRepository.findAll());
        allProducts.addAll(merchProductRepository.findAll());
        allProducts.addAll(specialProductRepository.findAll());
        return allProducts;
    }

    /**
     * Find all non-deleted products.
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findAllActive() {
        return findAll().stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    /**
     * Find all featured products.
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findFeatured() {
        List<BaseProduct> featuredProducts = new ArrayList<>();
        featuredProducts.addAll(beerProductRepository.findFeatured());
        featuredProducts.addAll(merchProductRepository.findFeatured());
        featuredProducts.addAll(specialProductRepository.findFeatured());
        return featuredProducts;
    }

    /**
     * Find products by kind (BEER, MERCH, SPECIAL).
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findByKind(ProductKind kind) {
        return switch (kind) {
            case BEER -> new ArrayList<>(beerProductRepository.findAll());
            case MERCH -> new ArrayList<>(merchProductRepository.findAll());
            case SPECIAL -> new ArrayList<>(specialProductRepository.findAll());
        };
    }

    /**
     * Save product (delegates to correct repository based on type).
     */
    @Transactional
    public BaseProduct save(BaseProduct product) {
        return switch (product.getProductKind()) {
            case BEER -> beerProductRepository.save((BeerProduct) product);
            case MERCH -> merchProductRepository.save((MerchProduct) product);
            case SPECIAL -> specialProductRepository.save((SpecialProduct) product);
        };
    }

    /**
     * Delete product by ID.
     */
    @Transactional
    public void delete(ProductId id) {
        beerProductRepository.delete(id);
        merchProductRepository.delete(id);
        specialProductRepository.delete(id);
    }

    /**
     * Check if product exists.
     */
    @Transactional(readOnly = true)
    public boolean exists(ProductId id) {
        return findById(id).isPresent();
    }

    /**
     * Search products by name (all types).
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> searchByName(String name) {
        List<BaseProduct> results = new ArrayList<>();
        results.addAll(beerProductRepository.searchByName(name));
        results.addAll(merchProductRepository.searchByName(name));
        results.addAll(specialProductRepository.searchByName(name));
        return results;
    }

    /**
     * Find products in stock.
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findInStock() {
        List<BaseProduct> inStock = new ArrayList<>();
        inStock.addAll(beerProductRepository.findInStock());
        inStock.addAll(merchProductRepository.findInStock());
        inStock.addAll(specialProductRepository.findInStock());
        return inStock;
    }

    /**
     * Find products with low stock.
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findLowStock() {
        List<BaseProduct> lowStock = new ArrayList<>();
        lowStock.addAll(beerProductRepository.findLowStock());
        lowStock.addAll(merchProductRepository.findLowStock());
        lowStock.addAll(specialProductRepository.findLowStock());
        return lowStock;
    }

    /**
     * Find all products with pagination (in-memory pagination for now).
     * TODO: Implement database-level pagination for better performance.
     */
    @Transactional(readOnly = true)
    public Page<BaseProduct> findAllPaginated(int pageNumber, int pageSize) {
        List<BaseProduct> allProducts = findAll();
        return paginateInMemory(allProducts, pageNumber, pageSize);
    }

    /**
     * Find featured products with pagination.
     */
    @Transactional(readOnly = true)
    public Page<BaseProduct> findFeaturedPaginated(int pageNumber, int pageSize) {
        List<BaseProduct> featured = findFeatured();
        return paginateInMemory(featured, pageNumber, pageSize);
    }

    /**
     * Find products by kind with pagination.
     */
    @Transactional(readOnly = true)
    public Page<BaseProduct> findByKindPaginated(ProductKind kind, int pageNumber, int pageSize) {
        List<BaseProduct> products = findByKind(kind);
        return paginateInMemory(products, pageNumber, pageSize);
    }

    /**
     * Search products by name with pagination.
     */
    @Transactional(readOnly = true)
    public Page<BaseProduct> searchByNamePaginated(String name, int pageNumber, int pageSize) {
        List<BaseProduct> results = searchByName(name);
        return paginateInMemory(results, pageNumber, pageSize);
    }

    /**
     * Helper method for in-memory pagination.
     */
    private Page<BaseProduct> paginateInMemory(List<BaseProduct> allItems, int pageNumber, int pageSize) {
        int totalElements = allItems.size();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, totalElements);

        if (start >= totalElements) {
            return new Page<>(List.of(), pageNumber, pageSize, totalElements);
        }

        List<BaseProduct> pageContent = allItems.subList(start, end);
        return new Page<>(pageContent, pageNumber, pageSize, totalElements);
    }

    /**
     * Find products by category.
     */
    @Transactional(readOnly = true)
    public List<BaseProduct> findByCategory(ProductCategory category) {
        ProductKind kind = switch (category) {
            case PRODUCTOS -> ProductKind.BEER;
            case MERCHANDISING -> ProductKind.MERCH;
            case ESPECIALES -> ProductKind.SPECIAL;
        };
        return findByKind(kind);
    }

    /**
     * Find products by category with pagination.
     */
    @Transactional(readOnly = true)
    public Page<BaseProduct> findByCategoryPaginated(ProductCategory category, int pageNumber, int pageSize) {
        List<BaseProduct> products = findByCategory(category);
        return paginateInMemory(products, pageNumber, pageSize);
    }
}
