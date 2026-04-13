package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.order.OrderId;
import com.bristol.domain.payment.Payment;
import com.bristol.domain.payment.PaymentId;
import com.bristol.domain.payment.PaymentProvider;
import com.bristol.domain.payment.PaymentStatus;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", expression = "java(toPaymentId(entity.getId()))")
    @Mapping(target = "paymentNumber", source = "paymentNumber")
    @Mapping(target = "orderId", expression = "java(toOrderId(entity.getOrderId()))")
    @Mapping(target = "userId", expression = "java(toUserId(entity.getUserId()))")
    @Mapping(target = "status", expression = "java(toDomainStatus(entity.getPaymentStatus()))")
    @Mapping(target = "provider", expression = "java(toDomainProvider(entity.getProvider()))")
    @Mapping(target = "amount", expression = "java(toMoney(entity.getAmount()))")
    Payment toDomain(PaymentEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "paymentNumber", source = "paymentNumber")
    @Mapping(target = "orderId", expression = "java(toUUID(domain.getOrderId()))")
    @Mapping(target = "userId", expression = "java(toUUID(domain.getUserId()))")
    @Mapping(target = "paymentStatus", expression = "java(toEntityStatus(domain.getStatus()))")
    @Mapping(target = "provider", expression = "java(toEntityProvider(domain.getProvider()))")
    @Mapping(target = "amount", expression = "java(toBigDecimal(domain.getAmount()))")
    PaymentEntity toEntity(Payment domain);

    default PaymentId toPaymentId(UUID uuid) {
        return uuid != null ? new PaymentId(uuid) : null;
    }

    default OrderId toOrderId(UUID uuid) {
        return uuid != null ? new OrderId(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default UUID toUUID(PaymentId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(OrderId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    default PaymentStatus toDomainStatus(PaymentEntity.PaymentStatusEnum status) {
        return status != null ? PaymentStatus.valueOf(status.name()) : null;
    }

    default PaymentEntity.PaymentStatusEnum toEntityStatus(PaymentStatus status) {
        return status != null ? PaymentEntity.PaymentStatusEnum.valueOf(status.name()) : null;
    }

    default PaymentProvider toDomainProvider(PaymentEntity.PaymentProviderEnum provider) {
        return provider != null ? PaymentProvider.valueOf(provider.name()) : null;
    }

    default PaymentEntity.PaymentProviderEnum toEntityProvider(PaymentProvider provider) {
        return provider != null ? PaymentEntity.PaymentProviderEnum.valueOf(provider.name()) : null;
    }
}
