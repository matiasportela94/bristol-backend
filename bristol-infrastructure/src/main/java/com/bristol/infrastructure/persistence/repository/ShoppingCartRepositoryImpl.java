package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.cart.ShoppingCart;
import com.bristol.domain.cart.ShoppingCartId;
import com.bristol.domain.cart.ShoppingCartRepository;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.CartItemEntity;
import com.bristol.infrastructure.persistence.mapper.CartItemMapper;
import com.bristol.infrastructure.persistence.mapper.ShoppingCartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

    private final JpaShoppingCartRepository jpaShoppingCartRepository;
    private final JpaCartItemRepository jpaCartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        var savedCart = jpaShoppingCartRepository.save(shoppingCartMapper.toEntity(cart));
        jpaCartItemRepository.deleteByCartId(savedCart.getId());

        List<CartItemEntity> itemEntities = cart.getItems().stream()
                .map(cartItemMapper::toEntity)
                .collect(Collectors.toList());
        jpaCartItemRepository.saveAll(itemEntities);

        return attachItems(shoppingCartMapper.toDomain(savedCart), cart.getItems().stream().collect(Collectors.toList()));
    }

    @Override
    public Optional<ShoppingCart> findById(ShoppingCartId cartId) {
        return jpaShoppingCartRepository.findById(cartId.getValue())
                .map(shoppingCartMapper::toDomain)
                .map(this::attachItems);
    }

    @Override
    public Optional<ShoppingCart> findByUserId(UserId userId) {
        return jpaShoppingCartRepository.findByUserId(userId.getValue())
                .map(shoppingCartMapper::toDomain)
                .map(this::attachItems);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return jpaShoppingCartRepository.existsByUserId(userId.getValue());
    }

    @Override
    public void delete(ShoppingCartId cartId) {
        jpaCartItemRepository.deleteByCartId(cartId.getValue());
        jpaShoppingCartRepository.deleteById(cartId.getValue());
    }

    private ShoppingCart attachItems(ShoppingCart cart) {
        return attachItems(cart, jpaCartItemRepository.findByCartId(cart.getId().getValue()).stream()
                .map(cartItemMapper::toDomain)
                .collect(Collectors.toList()));
    }

    private ShoppingCart attachItems(ShoppingCart cart, List<com.bristol.domain.cart.CartItem> items) {
        return cart.toBuilder()
                .items(items)
                .build();
    }
}
