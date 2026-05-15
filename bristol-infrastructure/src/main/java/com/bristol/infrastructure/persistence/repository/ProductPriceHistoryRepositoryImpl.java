package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductPriceHistory;
import com.bristol.domain.product.ProductPriceHistoryRepository;
import com.bristol.infrastructure.persistence.mapper.ProductPriceHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductPriceHistoryRepositoryImpl implements ProductPriceHistoryRepository {

    private final JpaProductPriceHistoryRepository jpaRepository;
    private final ProductPriceHistoryMapper mapper;

    @Override
    public ProductPriceHistory save(ProductPriceHistory entry) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(entry)));
    }

    @Override
    public List<ProductPriceHistory> findByProductId(ProductId productId) {
        return jpaRepository.findByProductIdOrderByChangedAtDesc(productId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
