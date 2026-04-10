package com.bristol.api.controller;

import com.bristol.application.cart.dto.*;
import com.bristol.application.cart.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final GetMyCartUseCase getMyCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final PreviewCartUseCase previewCartUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get current cart", description = "Retrieve the authenticated user's cart")
    public ResponseEntity<CartDto> getMyCart(Authentication authentication) {
        return ResponseEntity.ok(getMyCartUseCase.execute(authentication.getName()));
    }

    @PostMapping("/preview")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Preview current cart", description = "Preview the authenticated user's cart with an optional coupon applied")
    public ResponseEntity<CartDto> previewCart(
            @RequestBody(required = false) PreviewCartRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(previewCartUseCase.execute(authentication.getName(), request));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add item to cart", description = "Add a product to the authenticated user's cart")
    public ResponseEntity<CartDto> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(addItemToCartUseCase.execute(authentication.getName(), request));
    }

    @PatchMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an existing cart item")
    public ResponseEntity<CartDto> updateItemQuantity(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(updateCartItemQuantityUseCase.execute(authentication.getName(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remove cart item", description = "Remove an item from the authenticated user's cart")
    public ResponseEntity<Void> removeItem(@PathVariable String itemId, Authentication authentication) {
        removeCartItemUseCase.execute(authentication.getName(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Clear cart", description = "Remove all items from the authenticated user's cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        clearCartUseCase.execute(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Checkout cart", description = "Create an order from the authenticated user's cart")
    public ResponseEntity<CheckoutCartResponse> checkout(
            @Valid @RequestBody CheckoutCartRequest request,
            Authentication authentication
    ) {
        CheckoutCartResponse response = checkoutCartUseCase.execute(authentication.getName(), request);
        HttpStatus status = response.isCheckoutSucceeded() ? HttpStatus.OK : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(response);
    }
}
