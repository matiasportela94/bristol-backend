package com.bristol.application.delivery.route;

import java.util.List;

public interface RouteOptimizer {

    /**
     * Given an origin/depot and a list of waypoint addresses, returns them
     * in the optimized visit order. The returned list has the same size as
     * {@code waypointAddresses} and each element is the index into the original
     * list, e.g. [2, 0, 1] means "visit original[2] first, then [0], then [1]".
     *
     * Also populates per-leg distance and duration into {@code result}.
     */
    RouteOptimizerResult optimize(String depotAddress, List<String> waypointAddresses);
}
