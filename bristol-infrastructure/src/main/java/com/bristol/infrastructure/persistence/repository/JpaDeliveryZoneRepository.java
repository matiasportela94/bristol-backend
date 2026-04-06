package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.DeliveryZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DeliveryZoneEntity.
 */
@Repository
public interface JpaDeliveryZoneRepository extends JpaRepository<DeliveryZoneEntity, UUID> {

    List<DeliveryZoneEntity> findByIsActive(Boolean isActive);

    Optional<DeliveryZoneEntity> findByName(String name);

    boolean existsByName(String name);
}
