package com.bristol.application.order.service;

import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service for managing product stock in relation to orders.
 */
@Service
@RequiredArgsConstructor
public class StockManagementService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    /**
     * Deduct stock for all items in an order.
     */
    @Transactional
    public void deductStockForOrder(Order order) {
        if (order.isStockUpdated()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException(
                            "Product not found: " + item.getProductId().getValue()));

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

                productVariantRepository.save(variant.reduceStock(item.getQuantity(), Instant.now()));
                continue;
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new ValidationException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() +
                        ", Required: " + item.getQuantity());
            }

            productRepository.save(product.reduceStock(item.getQuantity(), Instant.now()));
        }
    }

    /**
     * Restore stock for all items in an order.
     */
    @Transactional
    public void restoreStockForOrder(Order order) {
        if (!order.isStockUpdated()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException(
                            "Product not found: " + item.getProductId().getValue()));

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
                        Instant.now()
                ));
                continue;
            }

            productRepository.save(product.increaseStock(item.getQuantity(), Instant.now()));
        }
    }
}
