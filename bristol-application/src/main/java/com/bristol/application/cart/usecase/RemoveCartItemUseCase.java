package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.CartItemId;
import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveCartItemUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(String userEmail, String itemId) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ValidationException("Cart not found for authenticated user"));

        shoppingCartRepository.save(cart.removeItem(new CartItemId(itemId), timeProvider.now()));
    }
}
