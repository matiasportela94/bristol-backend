package com.bristol.application.delivery.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DeliveryRouteDto {
    String date;
    String depot;
    int totalStops;
    int totalDistanceMeters;
    int totalDurationSeconds;
    List<DeliveryRouteStopDto> stops;
    /** Open directly on Google Maps — ready to share with the driver. */
    String googleMapsUrl;
}
