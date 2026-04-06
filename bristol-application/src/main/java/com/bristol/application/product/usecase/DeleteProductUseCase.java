package com.bristol.application.product.usecase;

import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case to delete a product (soft delete).
 */
@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public void execute(String productId) {
        ProductId id = new ProductId(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        Product deletedProduct = product.softDelete(Instant.now());
        productRepository.save(deletedProduct);
    }
}
