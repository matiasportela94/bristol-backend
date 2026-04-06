package com.bristol.application.cart.usecase;

import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ClearCartUseCase extends CartCommandSupport {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(String userEmail) {
        User user = resolveUserByEmail(userEmail, userRepository);
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> ShoppingCart.create(user.getId(), Instant.now()));

        shoppingCartRepository.save(cart.clear(Instant.now()));
    }
}
