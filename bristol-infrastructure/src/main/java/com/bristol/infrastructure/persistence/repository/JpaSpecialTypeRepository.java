package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.SpecialTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for SpecialTypeEntity.
 */
@Repository
public interface JpaSpecialTypeRepository extends JpaRepository<SpecialTypeEntity, UUID> {

    Optional<SpecialTypeEntity> findByCode(String code);

    List<SpecialTypeEntity> findByActiveTrue();

    List<SpecialTypeEntity> findAllByOrderByDisplayOrderAsc();

    boolean existsByCode(String code);
}
