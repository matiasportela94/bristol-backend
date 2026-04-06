package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for CouponEntity.
 */
@Repository
public interface JpaCouponRepository extends JpaRepository<CouponEntity, UUID> {

    Optional<CouponEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<CouponEntity> findByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    List<CouponEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    List<CouponEntity> findByStatusAndDeletedAtIsNull(CouponEntity.CouponStatusEnum status);

    List<CouponEntity> findByDiscountTypeAndDeletedAtIsNull(CouponEntity.CouponDiscountTypeEnum discountType);

    @Query("SELECT c FROM CouponEntity c WHERE c.deletedAt IS NULL AND c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :now) " +
           "AND (c.endDate IS NULL OR c.endDate >= :now)")
    List<CouponEntity> findActiveCoupons(LocalDateTime now);

    @Query("SELECT c FROM CouponEntity c WHERE c.deletedAt IS NULL AND c.method = 'AUTOMATIC' " +
           "AND c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :now) " +
           "AND (c.endDate IS NULL OR c.endDate >= :now)")
    List<CouponEntity> findAutomaticCoupons(LocalDateTime now);
}
