package com.bristol.domain.address;

import com.bristol.domain.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for UserAddress aggregate.
 */
public interface UserAddressRepository {

    UserAddress save(UserAddress address);

    Optional<UserAddress> findById(UserAddressId id);

    List<UserAddress> findByUserId(UserId userId);

    Optional<UserAddress> findDefaultByUserId(UserId userId);

    void delete(UserAddressId id);
}
