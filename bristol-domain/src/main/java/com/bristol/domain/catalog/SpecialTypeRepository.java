package com.bristol.domain.catalog;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for SpecialType aggregate.
 */
public interface SpecialTypeRepository {

    SpecialType save(SpecialType specialType);

    Optional<SpecialType> findById(SpecialTypeId id);

    Optional<SpecialType> findByCode(String code);

    List<SpecialType> findAll();

    List<SpecialType> findActive();

    boolean existsByCode(String code);

    void delete(SpecialTypeId id);
}
