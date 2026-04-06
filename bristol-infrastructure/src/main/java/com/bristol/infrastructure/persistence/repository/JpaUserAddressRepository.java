package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserAddressEntity.
 */
@Repository
public interface JpaUserAddressRepository extends JpaRepository<UserAddressEntity, UUID> {

    List<UserAddressEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    Optional<UserAddressEntity> findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(UUID userId);
}
