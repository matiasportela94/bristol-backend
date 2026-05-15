package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductPriceHistoryDto;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductPriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductPriceHistoryUseCase {

    private final ProductPriceHistoryRepository repository;

    @Transactional(readOnly = true)
    public List<ProductPriceHistoryDto> execute(String productId) {
        return repository.findByProductId(new ProductId(UUID.fromString(productId)))
                .stream()
                .map(entry -> ProductPriceHistoryDto.builder()
                        .id(entry.getId().asString())
                        .productId(entry.getProductId().asString())
                        .oldPrice(entry.getOldPrice() != null ? entry.getOldPrice().getAmount() : null)
                        .newPrice(entry.getNewPrice() != null ? entry.getNewPrice().getAmount() : null)
                        .changedAt(entry.getChangedAt())
                        .build())
                .toList();
    }
}
