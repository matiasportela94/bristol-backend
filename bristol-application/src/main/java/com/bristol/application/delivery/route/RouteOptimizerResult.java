package com.bristol.application.delivery.route;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RouteOptimizerResult {
    /** Optimized order: each element is the original index of the waypoint. */
    List<Integer> waypointOrder;
    /** Duration in seconds per leg (depot→stop1, stop1→stop2, ..., stopN→depot). */
    List<Integer> legDurationsSeconds;
    /** Distance in meters per leg, same order. */
    List<Integer> legDistancesMeters;
}
