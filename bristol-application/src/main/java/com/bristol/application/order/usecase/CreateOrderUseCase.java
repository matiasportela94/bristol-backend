package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.CreateOrderRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderItemRequest;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.*;
import com.bristol.domain.product.*;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case to create a new order.
 */
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto execute(CreateOrderRequest request) {
        Instant now = Instant.now();

        // Parse user ID
        UserId userId = new UserId(request.getUserId());

        // Create shipping address
        ShippingAddress shippingAddress = createShippingAddress(request);

        // Create order items
        List<OrderItem> orderItems = createOrderItems(request.getItems());

        // Create Money for shipping cost
        Money shippingCost = Money.of(request.getShippingCost());

        // Create order
        Order order = Order.create(
                userId,
                shippingAddress,
                orderItems,
                shippingCost,
                request.getNotes(),
                now
        );

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Convert to DTO and return
        return orderMapper.toDto(savedOrder);
    }

    private ShippingAddress createShippingAddress(CreateOrderRequest request) {
        var addressDto = request.getShippingAddress();
        DeliveryZoneId deliveryZoneId = new DeliveryZoneId(addressDto.getDeliveryZoneId());

        return ShippingAddress.of(
                addressDto.getAddressLine1(),
                addressDto.getAddressLine2(),
                addressDto.getCity(),
                addressDto.getProvince(),
                addressDto.getPostalCode(),
                deliveryZoneId
        );
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests) {
        // OrderItem requires a non-null order ID at construction time.
        // Order.create() will replace this placeholder with the final aggregate ID.
        List<OrderItem> items = new ArrayList<>();
        OrderId placeholderOrderId = OrderId.generate();

        for (OrderItemRequest itemRequest : itemRequests) {
            ProductId productId = new ProductId(itemRequest.getProductId());

            // Fetch product to get details
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ValidationException(
                            "Product not found: " + itemRequest.getProductId()));

            // Check if product is deleted
            if (product.getDeletedAt() != null) {
                throw new ValidationException("Product is no longer available: " + product.getName());
            }

            // Check stock availability
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new ValidationException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() +
                        ", Requested: " + itemRequest.getQuantity());
            }

            if (product.getBasePrice() == null) {
                throw new ValidationException(
                        "Product does not have a fixed price and cannot be ordered online: " + product.getName());
            }

            // Parse product variant ID if present
            ProductVariantId productVariantId = null;
            if (itemRequest.getProductVariantId() != null && !itemRequest.getProductVariantId().isBlank()) {
                productVariantId = new ProductVariantId(itemRequest.getProductVariantId());
            }

            // Map ProductCategory to ProductType
            ProductType productType = mapCategoryToType(product.getCategory());

            // Create order item
            OrderItem item = OrderItem.create(
                    placeholderOrderId,
                    productId,
                    productVariantId,
                    product.getName(),
                    productType,
                    product.getBeerType(),
                    itemRequest.getQuantity(),
                    product.getBasePrice()
            );

            items.add(item);
        }

        return items;
    }

    private ProductType mapCategoryToType(ProductCategory category) {
        return switch (category) {
            case PRODUCTOS -> ProductType.BEER;
            case MERCHANDISING -> ProductType.MERCH;
            case ESPECIALES -> ProductType.SPECIAL;
        };
    }
}
