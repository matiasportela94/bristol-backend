package com.bristol.application.geo;

import com.bristol.domain.shared.valueobject.Coordinates;

import java.util.Optional;

public interface AddressGeolocator {

    /**
     * Resolves a free-text address to geographic coordinates.
     * Returns empty if the address cannot be geocoded.
     */
    Optional<Coordinates> geolocate(String address);
}
