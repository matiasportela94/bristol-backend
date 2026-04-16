package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartAdjustmentDto;
import com.bristol.application.cart.dto.CartAdjustmentType;
import com.bristol.application.cart.dto.CheckoutCartRequest;
import com.bristol.application.cart.dto.CheckoutCartResponse;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.application.order.usecase.OrderMapper;
import com.bristol.application.order.usecase.OrderPromotionApplicationService;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderItem;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.ShippingAddress;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutCartUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserAddressRepository userAddressRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final OrderPromotionApplicationService orderPromotionApplicationService;
    private final CartReconciliationService cartReconciliationService;
    private final StockManagementService stockManagementService;
    private final TimeProvider timeProvider;

    @Transactional
    public CheckoutCartResponse execute(String userEmail, CheckoutCartRequest request) {
        Instant now = timeProvider.now();
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ValidationException("Cart not found for authenticated user"));

        if (cart.isEmpty()) {
            throw new ValidationException("Cart is empty");
        }

        CartReconciliationService.ReconciliationResult reconciliation = cartReconciliationService.reconcile(cart, now);
        if (reconciliation.hasChanges()) {
            ShoppingCart savedCart = shoppingCartRepository.save(reconciliation.cart());
            return CheckoutCartResponse.builder()
                    .checkoutSucceeded(false)
                    .message("El carrito fue actualizado antes de continuar.")
                    .cart(cartMapper.toDto(savedCart))
                    .adjustments(reconciliation.adjustments())
                    .createdOrder(null)
                    .build();
        }

        if (reconciliation.cart().isEmpty()) {
            ShoppingCart savedCart = shoppingCartRepository.save(reconciliation.cart());
            return CheckoutCartResponse.builder()
                    .checkoutSucceeded(false)
                    .message("El carrito no tiene productos disponibles para generar la orden.")
                    .cart(cartMapper.toDto(savedCart))
                    .adjustments(reconciliation.adjustments())
                    .createdOrder(null)
                    .build();
        }

        UserAddress address = userAddressRepository.findById(new UserAddressId(request.getShippingAddressId()))
                .filter(existing -> existing.getUserId().equals(user.getId()))
                .orElseThrow(() -> new ValidationException("Shipping address not found for authenticated user"));

        Order order = Order.create(
                user.getId(),
                toShippingAddress(address),
                toOrderItems(reconciliation.cart()),
                Money.zero(),
                request.getNotes(),
                now
        );

        Order orderWithPromotions;
        try {
            orderWithPromotions = orderPromotionApplicationService.applyRequestedPromotion(order, request.getCouponCode());
        } catch (ValidationException exception) {
            return CheckoutCartResponse.builder()
                    .checkoutSucceeded(false)
                    .message(exception.getMessage())
                    .cart(cartMapper.toDto(reconciliation.cart()))
                    .adjustments(buildPromotionAdjustments(request, exception.getMessage()))
                    .createdOrder(null)
                    .build();
        }

        stockManagementService.deductStockForOrder(orderWithPromotions);
        Order savedOrder = orderRepository.save(orderWithPromotions.markStockAsUpdated(now));
        ShoppingCart clearedCart = shoppingCartRepository.save(reconciliation.cart().clear(now));

        return CheckoutCartResponse.builder()
                .checkoutSucceeded(true)
                .message("Orden creada correctamente.")
                .cart(cartMapper.toDto(clearedCart))
                .adjustments(List.of())
                .createdOrder(toOrderDto(savedOrder))
                .build();
    }

    private ShippingAddress toShippingAddress(UserAddress address) {
        return ShippingAddress.of(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getDeliveryZoneId()
        );
    }

    private List<OrderItem> toOrderItems(ShoppingCart cart) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException("Product not found: " + item.getProductId().getValue()));
            orderItems.add(OrderItem.create(
                    com.bristol.domain.order.OrderId.generate(),
                    item.getProductId(),
                    item.getProductVariantId(),
                    item.getProductName(),
                    item.getProductType(),
                    item.getBeerType(),
                    product.getCategory(),
                    product.getSubcategory(),
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
        }
        return orderItems;
    }

    private OrderDto toOrderDto(Order order) {
        return orderMapper.toDto(order);
    }

    private List<CartAdjustmentDto> buildPromotionAdjustments(CheckoutCartRequest request, String message) {
        List<CartAdjustmentDto> adjustments = new ArrayList<>();

        if (hasText(request.getCouponCode())) {
            adjustments.add(CartAdjustmentDto.builder()
                    .type(CartAdjustmentType.PROMOTION_REMOVED)
                    .message(message)
                    .previousValue(request.getCouponCode().trim())
                    .currentValue(null)
                    .build());
        }

        return adjustments;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
