package com.bristol.infrastructure.persistence.repository;

import com.bristol.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    @Query(value = "SELECT nextval('payment_number_seq')", nativeQuery = true)
    Long nextPaymentNumber();

    List<PaymentEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

    List<PaymentEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<PaymentEntity> findByPaymentStatusOrderByCreatedAtDesc(PaymentEntity.PaymentStatusEnum status);

    Optional<PaymentEntity> findFirstByOrderIdAndPaymentStatusOrderByCreatedAtAsc(
            UUID orderId,
            PaymentEntity.PaymentStatusEnum status
    );

    List<PaymentEntity> findAllByOrderByCreatedAtDesc();
}
