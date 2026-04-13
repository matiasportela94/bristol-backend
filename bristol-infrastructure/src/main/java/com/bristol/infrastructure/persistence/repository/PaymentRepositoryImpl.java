package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.payment.Payment;
import com.bristol.domain.payment.PaymentId;
import com.bristol.domain.payment.PaymentRepository;
import com.bristol.domain.payment.PaymentStatus;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;
    private final PaymentMapper mapper;

    @Override
    public Payment save(Payment payment) {
        var entity = mapper.toEntity(payment);
        if (entity.getPaymentNumber() == null) {
            entity.setPaymentNumber(jpaRepository.nextPaymentNumber());
        }
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        return jpaRepository.findById(paymentId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findByOrderId(OrderId orderId) {
        return jpaRepository.findByOrderIdOrderByCreatedAtDesc(orderId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByUserId(UserId userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return jpaRepository.findByPaymentStatusOrderByCreatedAtDesc(
                        com.bristol.infrastructure.persistence.entity.PaymentEntity.PaymentStatusEnum.valueOf(status.name())
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findApprovedByOrderId(OrderId orderId) {
        return jpaRepository.findFirstByOrderIdAndPaymentStatusOrderByCreatedAtAsc(
                        orderId.getValue(),
                        com.bristol.infrastructure.persistence.entity.PaymentEntity.PaymentStatusEnum.APPROVED
                )
                .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
