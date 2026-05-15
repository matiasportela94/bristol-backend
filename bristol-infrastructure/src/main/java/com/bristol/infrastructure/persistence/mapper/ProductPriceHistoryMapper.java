package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductPriceHistory;
import com.bristol.domain.product.ProductPriceHistoryId;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.infrastructure.persistence.entity.ProductPriceHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPriceHistoryMapper {

    public ProductPriceHistory toDomain(ProductPriceHistoryEntity entity) {
        if (entity == null) return null;
        return ProductPriceHistory.builder()
                .id(new ProductPriceHistoryId(entity.getId()))
                .productId(new ProductId(entity.getProductId()))
                .oldPrice(entity.getOldPrice() != null ? Money.of(entity.getOldPrice()) : null)
                .newPrice(entity.getNewPrice() != null ? Money.of(entity.getNewPrice()) : null)
                .changedAt(entity.getChangedAt())
                .build();
    }

    public ProductPriceHistoryEntity toEntity(ProductPriceHistory domain) {
        if (domain == null) return null;
        return ProductPriceHistoryEntity.builder()
                .id(domain.getId().getValue())
                .productId(domain.getProductId().getValue())
                .oldPrice(domain.getOldPrice() != null ? domain.getOldPrice().getAmount() : null)
                .newPrice(domain.getNewPrice() != null ? domain.getNewPrice().getAmount() : null)
                .changedAt(domain.getChangedAt())
                .build();
    }
}
