package com.bristol.domain.distributor;

import com.bristol.domain.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Distributor aggregate.
 */
public interface DistributorRepository {

    Distributor save(Distributor distributor);

    Optional<Distributor> findById(DistributorId id);

    Optional<Distributor> findByUserId(UserId userId);

    Optional<Distributor> findByCuit(String cuit);

    List<Distributor> findByStatus(DistributorStatus status);

    List<Distributor> findAll();

    boolean existsByCuit(String cuit);

    boolean existsByUserId(UserId userId);

    void delete(DistributorId id);
}
