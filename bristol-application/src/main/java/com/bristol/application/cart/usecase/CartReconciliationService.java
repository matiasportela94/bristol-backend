package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartAdjustmentDto;
import com.bristol.application.cart.dto.CartAdjustmentType;
import com.bristol.application.product.service.UnifiedProductService;
import com.bristol.domain.cart.CartItem;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.product.BaseProduct;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartReconciliationService extends CartCommandSupport {

    private final UnifiedProductService unifiedProductService;
    private final ProductVariantRepository productVariantRepository;

    public ReconciliationResult reconcile(ShoppingCart cart, Instant now) {
        List<CartAdjustmentDto> adjustments = new ArrayList<>();
        List<CartItem> reconciledItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            BaseProduct product = unifiedProductService.findById(item.getProductId()).orElse(null);
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
                    .filter(v -> v.getProductId().equals(product.getId()));

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

        return new ReconciliationResult(cart.replaceItems(reconciledItems, now), adjustments);
    }

    public record ReconciliationResult(ShoppingCart cart, List<CartAdjustmentDto> adjustments) {
        public boolean hasChanges() {
            return !adjustments.isEmpty();
        }
    }
}
