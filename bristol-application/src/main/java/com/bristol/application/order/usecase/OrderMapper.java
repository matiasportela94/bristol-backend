package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderItemDto;
import com.bristol.application.order.dto.ShippingAddressDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert between Order domain entity and OrderDto.
 */
@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        return toDto(order, null, null, null, null);
    }

    public OrderDto toDto(Order order, String distributorName) {
        return toDto(order, null, null, distributorName, null);
    }

    public OrderDto toDto(Order order, String customerName, String userEmail, String distributorName) {
        return toDto(order, customerName, userEmail, distributorName, null);
    }

    public OrderDto toDto(Order order, String customerName, String userEmail, String distributorName, Delivery delivery) {
        return OrderDto.builder()
                .id(order.getId().getValue().toString())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId().getValue().toString())
                .customerName(customerName)
                .userEmail(userEmail)
                .distributorName(distributorName)
                .status(order.getStatus())
                .shippingAddress(toShippingAddressDto(order.getShippingAddress()))
                .items(order.getItems().stream()
                        .map(this::toOrderItemDto)
                        .collect(Collectors.toList()))
                .subtotal(order.getSubtotal().getAmount())
                .orderDiscountAmount(order.getOrderDiscountAmount().getAmount())
                .shippingCost(order.getShippingCost().getAmount())
                .shippingDiscountAmount(order.getShippingDiscountAmount().getAmount())
                .total(order.getTotal().getAmount())
                .stockUpdated(order.isStockUpdated())
                .notes(order.getNotes())
                .scheduledDelivery(delivery != null ? delivery.getScheduledDate() : null)
                .deliveryStatus(delivery != null ? delivery.getStatus().name() : null)
                .orderDate(order.getOrderDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemDto toOrderItemDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId().getValue().toString())
                .productId(item.getProductId().getValue().toString())
                .productVariantId(item.getProductVariantId() != null ? item.getProductVariantId().getValue().toString() : null)
                .productName(item.getProductName())
                .productType(item.getProductType())
                .beerType(item.getBeerType())
                .productCategory(item.getProductCategory())
                .productSubcategory(item.getProductSubcategory())
                .quantity(item.getQuantity())
                .pricePerUnit(item.getPricePerUnit().getAmount())
                .itemDiscountAmount(item.getItemDiscountAmount().getAmount())
                .subtotal(item.getSubtotal().getAmount())
                .build();
    }

    private ShippingAddressDto toShippingAddressDto(com.bristol.domain.order.ShippingAddress address) {
        return ShippingAddressDto.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryZoneId(address.getDeliveryZoneId().getValue().toString())
                .build();
    }
}
