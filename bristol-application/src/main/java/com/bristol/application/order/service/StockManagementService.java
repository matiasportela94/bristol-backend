package com.bristol.application.order.service;

import com.bristol.application.brewery.service.BreweryInventoryService;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.BeerProduct;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockManagementService {

    private final UnifiedProductService unifiedProductService;
    private final ProductVariantRepository productVariantRepository;
    private final BreweryInventoryService breweryInventoryService;
    private final TimeProvider timeProvider;

    @Transactional
    public void deductStockForOrder(Order order) {
        if (order.isStockUpdated()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            BaseProduct product = unifiedProductService.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException(
                            "Product not found: " + item.getProductId().getValue()));

            // Beer stock is managed exclusively via brewery_inventory — skip product.stockQuantity check
            if (product instanceof BeerProduct beerProduct && item.getProductVariantId() == null) {
                breweryInventoryService.deductCansForSale(beerProduct, item.getQuantity(), order.getId().getValue());
                continue;
            }

            if (item.getProductVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId())
                        .orElseThrow(() -> new ValidationException(
                                "Product variant not found: " + item.getProductVariantId().getValue()));

                if (!variant.getProductId().equals(product.getId())) {
                    throw new ValidationException(
                            "Variant does not belong to product: " + item.getProductVariantId().getValue());
                }

                if (variant.getStockQuantity() < item.getQuantity()) {
                    throw new ValidationException(
                            "Insufficient stock for product: " + product.getName() +
                            ". Available: " + variant.getStockQuantity() +
                            ", Required: " + item.getQuantity());
                }

                productVariantRepository.save(variant.reduceStock(item.getQuantity(), timeProvider.now()));
                continue;
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new ValidationException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() +
                        ", Required: " + item.getQuantity());
            }

            unifiedProductService.save(product.reduceStock(item.getQuantity(), timeProvider.now()));
        }
    }

    @Transactional
    public void restoreStockForOrder(Order order) {
        if (!order.isStockUpdated()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            BaseProduct product = unifiedProductService.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException(
                            "Product not found: " + item.getProductId().getValue()));

            // Beer stock restored exclusively via brewery_inventory
            if (product instanceof BeerProduct beerProduct && item.getProductVariantId() == null) {
                breweryInventoryService.restoreCansForCancellation(beerProduct, item.getQuantity(), order.getId().getValue());
                continue;
            }

            if (item.getProductVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId())
                        .orElseThrow(() -> new ValidationException(
                                "Product variant not found: " + item.getProductVariantId().getValue()));

                if (!variant.getProductId().equals(product.getId())) {
                    throw new ValidationException(
                            "Variant does not belong to product: " + item.getProductVariantId().getValue());
                }

                productVariantRepository.save(variant.updateStock(
                        variant.getStockQuantity() + item.getQuantity(),
                        timeProvider.now()
                ));
                continue;
            }

            unifiedProductService.save(product.increaseStock(item.getQuantity(), timeProvider.now()));
        }
    }
}
