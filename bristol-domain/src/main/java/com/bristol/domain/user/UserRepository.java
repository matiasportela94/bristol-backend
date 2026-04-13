package com.bristol.domain.user;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for User aggregate.
 * To be implemented by infrastructure layer.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean existsByEmail(String email);

    void delete(UserId id);
}
