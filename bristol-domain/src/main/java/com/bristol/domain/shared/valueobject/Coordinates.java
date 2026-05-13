package com.bristol.domain.shared.valueobject;

public record Coordinates(double lat, double lng) {

    public Coordinates {
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("Invalid latitude: " + lat);
        if (lng < -180 || lng > 180) throw new IllegalArgumentException("Invalid longitude: " + lng);
    }
}
