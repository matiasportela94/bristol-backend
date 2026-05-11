package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.MerchTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for MerchTypeEntity.
 */
@Repository
public interface JpaMerchTypeRepository extends JpaRepository<MerchTypeEntity, UUID> {

    Optional<MerchTypeEntity> findByCode(String code);

    List<MerchTypeEntity> findByCategory(MerchTypeEntity.MerchCategoryEnum category);

    List<MerchTypeEntity> findByActiveTrue();

    List<MerchTypeEntity> findAllByOrderByDisplayOrderAsc();

    boolean existsByCode(String code);
}
