package com.bristol.infrastructure.persistence.mapper;

import com.bristol.domain.coupon.CouponId;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MapStruct mapper for Order domain object and OrderEntity.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", expression = "java(toOrderId(entity.getId()))")
    @Mapping(target = "orderNumber", source = "orderNumber")
    @Mapping(target = "userId", expression = "java(toUserId(entity.getUserId()))")
    @Mapping(target = "status", expression = "java(toDomainStatus(entity.getOrderStatus()))")
    @Mapping(target = "distributorId", expression = "java(toDistributorId(entity.getDistributorId()))")
    @Mapping(target = "branchId", expression = "java(toBranchId(entity.getBranchId()))")
    @Mapping(target = "shippingAddress", expression = "java(toShippingAddress(entity))")
    @Mapping(target = "subtotal", expression = "java(toMoney(entity.getSubtotal()))")
    @Mapping(target = "orderDiscountCouponId", expression = "java(toCouponId(entity.getOrderDiscountCouponId()))")
    @Mapping(target = "orderDiscountAmount", expression = "java(toMoney(entity.getOrderDiscountAmount()))")
    @Mapping(target = "shippingCost", expression = "java(toMoney(entity.getShippingCost()))")
    @Mapping(target = "shippingDiscountCouponId", expression = "java(toCouponId(entity.getShippingDiscountCouponId()))")
    @Mapping(target = "shippingDiscountAmount", expression = "java(toMoney(entity.getShippingDiscountAmount()))")
    @Mapping(target = "total", expression = "java(toMoney(entity.getTotal()))")
    @Mapping(target = "items", ignore = true) // Items are loaded separately
    Order toDomain(OrderEntity entity);

    @Mapping(target = "id", expression = "java(toUUID(domain.getId()))")
    @Mapping(target = "orderNumber", source = "orderNumber")
    @Mapping(target = "userId", expression = "java(toUUID(domain.getUserId()))")
    @Mapping(target = "orderStatus", expression = "java(toEntityStatus(domain.getStatus()))")
    @Mapping(target = "distributorId", expression = "java(toUUID(domain.getDistributorId()))")
    @Mapping(target = "branchId", expression = "java(toUUID(domain.getBranchId()))")
    @Mapping(target = "shippingAddressLine1", expression = "java(domain.getShippingAddress().getAddressLine1())")
    @Mapping(target = "shippingAddressLine2", expression = "java(domain.getShippingAddress().getAddressLine2())")
    @Mapping(target = "shippingCity", expression = "java(domain.getShippingAddress().getCity())")
    @Mapping(target = "shippingProvince", expression = "java(domain.getShippingAddress().getProvince())")
    @Mapping(target = "shippingPostalCode", expression = "java(domain.getShippingAddress().getPostalCode())")
    @Mapping(target = "deliveryZoneId", expression = "java(toUUID(domain.getShippingAddress().getDeliveryZoneId()))")
    @Mapping(target = "subtotal", expression = "java(toBigDecimal(domain.getSubtotal()))")
    @Mapping(target = "orderDiscountCouponId", expression = "java(toUUID(domain.getOrderDiscountCouponId()))")
    @Mapping(target = "orderDiscountAmount", expression = "java(toBigDecimal(domain.getOrderDiscountAmount()))")
    @Mapping(target = "shippingCost", expression = "java(toBigDecimal(domain.getShippingCost()))")
    @Mapping(target = "shippingDiscountCouponId", expression = "java(toUUID(domain.getShippingDiscountCouponId()))")
    @Mapping(target = "shippingDiscountAmount", expression = "java(toBigDecimal(domain.getShippingDiscountAmount()))")
    @Mapping(target = "total", expression = "java(toBigDecimal(domain.getTotal()))")
    OrderEntity toEntity(Order domain);

    // ID conversions
    default OrderId toOrderId(UUID uuid) {
        return uuid != null ? new OrderId(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? new UserId(uuid) : null;
    }

    default DistributorId toDistributorId(UUID uuid) {
        return uuid != null ? new DistributorId(uuid) : null;
    }

    default DistributorBranchId toBranchId(UUID uuid) {
        return uuid != null ? new DistributorBranchId(uuid) : null;
    }

    default CouponId toCouponId(UUID uuid) {
        return uuid != null ? new CouponId(uuid) : null;
    }

    default DeliveryZoneId toDeliveryZoneId(UUID uuid) {
        return uuid != null ? new DeliveryZoneId(uuid) : null;
    }

    default UUID toUUID(OrderId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(UserId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DistributorId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DistributorBranchId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(CouponId id) {
        return id != null ? id.getValue() : null;
    }

    default UUID toUUID(DeliveryZoneId id) {
        return id != null ? id.getValue() : null;
    }

    // Money conversions
    default Money toMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    default BigDecimal toBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    // Status conversions
    default OrderStatus toDomainStatus(OrderEntity.OrderStatusEnum status) {
        return status != null ? OrderStatus.valueOf(status.name()) : null;
    }

    default OrderEntity.OrderStatusEnum toEntityStatus(OrderStatus status) {
        return status != null ? OrderEntity.OrderStatusEnum.valueOf(status.name()) : null;
    }

    // ShippingAddress conversion
    default ShippingAddress toShippingAddress(OrderEntity entity) {
        if (entity == null) return null;
        return ShippingAddress.of(
                entity.getShippingAddressLine1(),
                entity.getShippingAddressLine2(),
                entity.getShippingCity(),
                entity.getShippingProvince(),
                entity.getShippingPostalCode(),
                toDeliveryZoneId(entity.getDeliveryZoneId())
        );
    }
}
