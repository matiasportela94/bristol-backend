package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserAddressRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class UserAddressRepositoryImpl implements UserAddressRepository {

    private final JpaUserAddressRepository jpaRepository;
    private final UserAddressMapper mapper;

    @Override
    public UserAddress save(UserAddress address) {
        var entity = mapper.toEntity(address);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserAddress> findById(UserAddressId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<UserAddress> findByUserId(UserId userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserAddress> findDefaultByUserId(UserId userId) {
        return jpaRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public void delete(UserAddressId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
