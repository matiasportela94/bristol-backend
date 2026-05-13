package com.bristol.application.delivery.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeliveryRouteStopDto {
    int stopNumber;
    String deliveryId;
    Long deliveryNumber;
    String orderId;
    Long orderNumber;
    String customerName;
    String address;
    String estimatedArrival;
    int legDistanceMeters;
    int legDurationSeconds;
}
