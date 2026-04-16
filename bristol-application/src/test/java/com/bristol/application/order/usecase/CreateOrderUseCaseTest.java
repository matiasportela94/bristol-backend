package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.CreateOrderRequest;
import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderItemRequest;
import com.bristol.application.order.dto.ShippingAddressDto;
import com.bristol.application.order.service.StockManagementService;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderUseCaseTest {

    @Test
    void executeShouldReserveStockWhenOrderIsCreated() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);
        StockManagementService stockManagementService = mock(StockManagementService.class);
        OrderMapper orderMapper = new OrderMapper();
        OrderPromotionApplicationService orderPromotionApplicationService = mock(OrderPromotionApplicationService.class);

        CreateOrderUseCase useCase = new CreateOrderUseCase(
                orderRepository,
                productRepository,
                productVariantRepository,
                stockManagementService,
                orderMapper,
                orderPromotionApplicationService,
                fixedTimeProvider()
        );

        Instant now = Instant.parse("2026-04-09T12:00:00Z");
        UserId userId = UserId.generate();
        Product product = Product.create(
                "IPA",
                "IPA 473",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.CAN,
                BeerType.IPA,
                Money.of(100),
                10,
                2,
                now
        );

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(userId.getValue().toString())
                .items(List.of(OrderItemRequest.builder()
                        .productId(product.getId().getValue().toString())
                        .productVariantId("")
                        .quantity(2)
                        .build()))
                .shippingAddress(ShippingAddressDto.builder()
                        .addressLine1("Calle 123")
                        .city("La Plata")
                        .province("Buenos Aires")
                        .postalCode("1900")
                        .deliveryZoneId(DeliveryZoneId.generate().getValue().toString())
                        .build())
                .shippingCost(java.math.BigDecimal.ZERO)
                .build();

        when(productRepository.findById(new ProductId(product.getId().getValue().toString()))).thenReturn(Optional.of(product));
        when(orderPromotionApplicationService.applyRequestedPromotions(any(Order.class), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = useCase.execute(request);

        assertThat(result.isStockUpdated()).isTrue();
        assertThat(result.getStatus()).isEqualTo(com.bristol.domain.order.OrderStatus.PENDING_PAYMENT);
        verify(stockManagementService).deductStockForOrder(any(Order.class));
    }

    private static TimeProvider fixedTimeProvider() {
        Instant fixedInstant = Instant.parse("2026-04-09T12:00:00Z");
        return new TimeProvider() {
            @Override
            public Instant now() {
                return fixedInstant;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.of(2026, 4, 9, 9, 0);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 9);
            }
        };
    }
}
