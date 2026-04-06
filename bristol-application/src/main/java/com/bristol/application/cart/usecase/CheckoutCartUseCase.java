package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.*;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.usecase.OrderMapper;
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
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Transactional
    public CheckoutCartResponse execute(String userEmail, CheckoutCartRequest request) {
        Instant now = Instant.now();
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ValidationException("Cart not found for authenticated user"));

        if (cart.isEmpty()) {
            throw new ValidationException("Cart is empty");
        }

        ReconciliationResult reconciliation = reconcileCart(cart, now);
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

        Order savedOrder = orderRepository.save(order);
        ShoppingCart clearedCart = shoppingCartRepository.save(reconciliation.cart().clear(now));

        return CheckoutCartResponse.builder()
                .checkoutSucceeded(true)
                .message("Orden creada correctamente.")
                .cart(cartMapper.toDto(clearedCart))
                .adjustments(List.of())
                .createdOrder(toOrderDto(savedOrder))
                .build();
    }

    private ReconciliationResult reconcileCart(ShoppingCart cart, Instant now) {
        List<CartAdjustmentDto> adjustments = new ArrayList<>();
        List<CartItem> reconciledItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null || product.isDeleted() || product.getBasePrice() == null) {
                adjustments.add(CartAdjustmentDto.builder()
                        .type(CartAdjustmentType.ITEM_UNAVAILABLE)
                        .itemId(item.getId().getValue().toString())
                        .productId(item.getProductId().getValue().toString())
                        .message("El producto ya no se encuentra disponible.")
                        .previousValue(item.getProductName())
                        .currentValue(null)
                        .build());
                continue;
            }

            Optional<ProductVariant> variant = Optional.ofNullable(item.getProductVariantId())
                    .flatMap(productVariantRepository::findById)
                    .filter(productVariant -> productVariant.getProductId().equals(product.getId()));

            if (item.getProductVariantId() != null && variant.isEmpty()) {
                adjustments.add(CartAdjustmentDto.builder()
                        .type(CartAdjustmentType.ITEM_UNAVAILABLE)
                        .itemId(item.getId().getValue().toString())
                        .productId(item.getProductId().getValue().toString())
                        .message("La variante seleccionada ya no está disponible.")
                        .previousValue(item.getProductVariantId().getValue().toString())
                        .currentValue(null)
                        .build());
                continue;
            }

            Money resolvedUnitPrice = resolveUnitPrice(product, variant);
            int availableStock = variant.map(ProductVariant::getStockQuantity).orElse(product.getStockQuantity());
            if (availableStock <= 0) {
                adjustments.add(CartAdjustmentDto.builder()
                        .type(CartAdjustmentType.ITEM_OUT_OF_STOCK)
                        .itemId(item.getId().getValue().toString())
                        .productId(item.getProductId().getValue().toString())
                        .message("El producto quedó sin stock.")
                        .previousValue(String.valueOf(item.getQuantity()))
                        .currentValue("0")
                        .build());
                continue;
            }

            int reconciledQuantity = Math.min(item.getQuantity(), availableStock);
            if (reconciledQuantity != item.getQuantity()) {
                adjustments.add(CartAdjustmentDto.builder()
                        .type(CartAdjustmentType.QUANTITY_ADJUSTED)
                        .itemId(item.getId().getValue().toString())
                        .productId(item.getProductId().getValue().toString())
                        .message("La cantidad fue ajustada al stock disponible.")
                        .previousValue(String.valueOf(item.getQuantity()))
                        .currentValue(String.valueOf(reconciledQuantity))
                        .build());
            }

            if (!Objects.equals(item.getUnitPrice().getAmount(), resolvedUnitPrice.getAmount())) {
                adjustments.add(CartAdjustmentDto.builder()
                        .type(CartAdjustmentType.PRICE_CHANGED)
                        .itemId(item.getId().getValue().toString())
                        .productId(item.getProductId().getValue().toString())
                        .message("El precio del producto cambió.")
                        .previousValue(item.getUnitPrice().getAmount().toPlainString())
                        .currentValue(resolvedUnitPrice.getAmount().toPlainString())
                        .build());
            }

            reconciledItems.add(item.updateSnapshot(
                    product.getName(),
                    mapCategoryToType(product.getCategory()),
                    product.getBeerType(),
                    reconciledQuantity,
                    resolvedUnitPrice
            ));
        }

        ShoppingCart reconciledCart = cart.replaceItems(reconciledItems, now);
        return new ReconciliationResult(reconciledCart, adjustments);
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
            orderItems.add(OrderItem.create(
                    com.bristol.domain.order.OrderId.generate(),
                    item.getProductId(),
                    item.getProductVariantId(),
                    item.getProductName(),
                    item.getProductType(),
                    item.getBeerType(),
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
        }
        return orderItems;
    }

    private OrderDto toOrderDto(Order order) {
        return orderMapper.toDto(order);
    }

    private record ReconciliationResult(ShoppingCart cart, List<CartAdjustmentDto> adjustments) {
        boolean hasChanges() {
            return !adjustments.isEmpty();
        }
    }
}
