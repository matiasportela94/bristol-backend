package com.bristol.domain.cart;

import com.bristol.domain.user.UserId;

import java.util.Optional;

/**
 * Repository port for ShoppingCart aggregate.
 */
public interface ShoppingCartRepository {

    ShoppingCart save(ShoppingCart cart);

    Optional<ShoppingCart> findById(ShoppingCartId cartId);

    Optional<ShoppingCart> findByUserId(UserId userId);

    boolean existsByUserId(UserId userId);

    void delete(ShoppingCartId cartId);
}
