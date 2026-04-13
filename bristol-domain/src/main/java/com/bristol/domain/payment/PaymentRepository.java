package com.bristol.domain.payment;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId paymentId);

    List<Payment> findByOrderId(OrderId orderId);

    List<Payment> findByUserId(UserId userId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findApprovedByOrderId(OrderId orderId);

    List<Payment> findAll();
}
