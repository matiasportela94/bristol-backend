package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.coupon.Coupon;
import com.bristol.domain.coupon.CouponDiscountType;
import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.coupon.CouponRepository;
import com.bristol.domain.coupon.CouponStatus;
import com.bristol.infrastructure.persistence.entity.CouponEntity;
import com.bristol.infrastructure.persistence.mapper.CouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CouponRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final JpaCouponRepository jpaRepository;
    private final CouponMapper mapper;

    @Override
    public Coupon save(Coupon coupon) {
        var entity = mapper.toEntity(coupon);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(CouponId id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return jpaRepository.findByCodeIgnoreCaseAndDeletedAtIsNull(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return jpaRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coupon> findActive() {
        return jpaRepository.findActiveCoupons(LocalDateTime.now()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coupon> findByStatus(CouponStatus status) {
        var entityStatus = switch (status) {
            case ACTIVE -> CouponEntity.CouponStatusEnum.ACTIVE;
            case PAUSED -> CouponEntity.CouponStatusEnum.INACTIVE;
            case EXPIRED -> CouponEntity.CouponStatusEnum.EXPIRED;
        };
        return jpaRepository.findByStatusAndDeletedAtIsNull(entityStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coupon> findByDiscountType(CouponDiscountType discountType) {
        var entityDiscountType = CouponEntity.CouponDiscountTypeEnum.valueOf(discountType.name());
        return jpaRepository.findByDiscountTypeAndDeletedAtIsNull(entityDiscountType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coupon> findAutomatic() {
        return jpaRepository.findAutomaticCoupons(LocalDateTime.now()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CouponId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
