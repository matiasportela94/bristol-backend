package com.bristol.application.product.usecase;

import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final UnifiedProductService unifiedProductService;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(String productId) {
        ProductId id = new ProductId(productId);
        BaseProduct product = unifiedProductService.findById(id)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        unifiedProductService.save(product.softDelete(timeProvider.now()));
    }
}
