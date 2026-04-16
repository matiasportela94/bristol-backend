package com.bristol.application.cart.usecase;

import com.bristol.application.cart.dto.CartDto;
import com.bristol.application.cart.dto.PreviewCartRequest;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreviewCartUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final CartReconciliationService cartReconciliationService;
    private final TimeProvider timeProvider;

    @Transactional
    public CartDto execute(String userEmail, PreviewCartRequest request) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> shoppingCartRepository.save(ShoppingCart.create(user.getId(), timeProvider.now())));
        CartReconciliationService.ReconciliationResult reconciliation =
                cartReconciliationService.reconcile(cart, timeProvider.now());
        ShoppingCart effectiveCart = reconciliation.hasChanges()
                ? shoppingCartRepository.save(reconciliation.cart())
                : cart;
        return cartMapper.toDto(effectiveCart, request != null ? request.getCouponCode() : null);
    }
}
