package com.bristol.domain.catalog;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for MerchType aggregate.
 */
public interface MerchTypeRepository {

    MerchType save(MerchType merchType);

    Optional<MerchType> findById(MerchTypeId id);

    Optional<MerchType> findByCode(String code);

    List<MerchType> findAll();

    List<MerchType> findByCategory(MerchCategory category);

    List<MerchType> findActive();

    boolean existsByCode(String code);

    void delete(MerchTypeId id);
}
