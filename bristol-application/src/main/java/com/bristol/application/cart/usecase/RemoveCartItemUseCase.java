package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.CartItemId;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(String userEmail, String itemId) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ValidationException("Cart not found for authenticated user"));

        shoppingCartRepository.save(cart.removeItem(new CartItemId(itemId), Instant.now()));
    }
}
