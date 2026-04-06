package com.bristol.domain.delivery;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for DeliveryZone aggregate.
 */
public interface DeliveryZoneRepository {

    DeliveryZone save(DeliveryZone zone);

    Optional<DeliveryZone> findById(DeliveryZoneId id);

    Optional<DeliveryZone> findByName(String name);

    List<DeliveryZone> findAll();

    List<DeliveryZone> findAllActive();

    boolean existsByName(String name);

    void delete(DeliveryZoneId id);
}
