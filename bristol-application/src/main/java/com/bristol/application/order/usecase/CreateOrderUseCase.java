package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.CreateOrderRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderItemRequest;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.*;
import com.bristol.domain.product.*;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final UnifiedProductService unifiedProductService;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final StockManagementService stockManagementService;
    private final OrderMapper orderMapper;
    private final OrderPromotionApplicationService orderPromotionApplicationService;
    private final TimeProvider timeProvider;

    @Transactional
    public OrderDto execute(CreateOrderRequest request) {
        Instant now = timeProvider.now();

        UserId userId = new UserId(request.getUserId());
        ShippingAddress shippingAddress = createShippingAddress(request);
        List<OrderItem> orderItems = createOrderItems(request.getItems());
        Money shippingCost = Money.of(request.getShippingCost());
        User user = userRepository.findById(userId).orElse(null);

        Order order = Order.create(userId, shippingAddress, orderItems, shippingCost, request.getNotes(), now);

        if (user != null && user.getBranchId() != null) {
            order = order.toBuilder().branchId(user.getBranchId()).build();
        }

        order = orderPromotionApplicationService.applyRequestedPromotions(
                order,
                request.getOrderDiscountCouponCode(),
                request.getShippingDiscountCouponCode()
        );

        stockManagementService.deductStockForOrder(order);
        Order savedOrder = orderRepository.save(order.markStockAsUpdated(now));
        return orderMapper.toDto(savedOrder);
    }

    private ShippingAddress createShippingAddress(CreateOrderRequest request) {
        var addressDto = request.getShippingAddress();
        return ShippingAddress.of(
                addressDto.getAddressLine1(),
                addressDto.getAddressLine2(),
                addressDto.getCity(),
                addressDto.getProvince(),
                addressDto.getPostalCode(),
                new DeliveryZoneId(addressDto.getDeliveryZoneId())
        );
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests) {
        List<OrderItem> items = new ArrayList<>();
        OrderId placeholderOrderId = OrderId.generate();

        for (OrderItemRequest itemRequest : itemRequests) {
            ProductId productId = new ProductId(itemRequest.getProductId());

            BaseProduct product = unifiedProductService.findById(productId)
                    .orElseThrow(() -> new ValidationException("Product not found: " + itemRequest.getProductId()));

            if (product.isDeleted()) {
                throw new ValidationException("Product is no longer available: " + product.getName());
            }
            if (product.getBasePrice() == null) {
                throw new ValidationException(
                        "Product does not have a fixed price and cannot be ordered online: " + product.getName());
            }

            ProductVariantId productVariantId = null;
            ProductVariant variant = null;
            if (itemRequest.getProductVariantId() != null && !itemRequest.getProductVariantId().isBlank()) {
                productVariantId = new ProductVariantId(itemRequest.getProductVariantId());
                variant = productVariantRepository.findById(productVariantId)
                        .orElseThrow(() -> new ValidationException(
                                "Product variant not found: " + itemRequest.getProductVariantId()));
                if (!variant.getProductId().equals(productId)) {
                    throw new ValidationException("Variant does not belong to product: " + itemRequest.getProductVariantId());
                }
            }

            int availableStock = variant != null ? variant.getStockQuantity() : product.getStockQuantity();
            if (availableStock < itemRequest.getQuantity()) {
                throw new ValidationException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + availableStock +
                        ", Requested: " + itemRequest.getQuantity());
            }

            Money unitPrice = variant != null
                    ? product.getBasePrice().add(variant.getAdditionalPrice())
                    : product.getBasePrice();

            items.add(OrderItem.create(
                    placeholderOrderId,
                    productId,
                    productVariantId,
                    product.getName(),
                    mapCategoryToType(product.getCategory()),
                    product.getBeerType(),
                    product.getCategory(),
                    product.getSubcategory(),
                    itemRequest.getQuantity(),
                    unitPrice
            ));
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
