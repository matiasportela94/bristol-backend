package com.bristol.application.order.service;

import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
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

    /**
     * Deduct stock for all items in an order.
     * Called when order is marked as paid.
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

            // Check if product has enough stock
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new ValidationException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() +
                        ", Required: " + item.getQuantity());
            }

            // Deduct stock
            int newStock = product.getStockQuantity() - item.getQuantity();
            Product updatedProduct = product.updateStock(newStock, Instant.now());
            productRepository.save(updatedProduct);
        }
    }

    /**
     * Restore stock for all items in an order.
     * Called when order is cancelled.
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

            // Restore stock
            int newStock = product.getStockQuantity() + item.getQuantity();
            Product updatedProduct = product.updateStock(newStock, Instant.now());
            productRepository.save(updatedProduct);
        }
    }
}
