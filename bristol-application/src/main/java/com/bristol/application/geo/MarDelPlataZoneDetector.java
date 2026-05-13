package com.bristol.application.geo;

import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.shared.valueobject.Coordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Detects which Bristol delivery zone a coordinate belongs to.
 *
 * Zone definitions for Mar del Plata:
 *
 *   CENTRO — quadrilateral bounded by:
 *              Av. Independencia (N), Av. Juan B. Justo (S),
 *              Av. Luro (W), costa (E)
 *   NORTE  — everything north of Av. Independencia
 *   SUR    — everything south of Av. Juan B. Justo
 *
 * The 4 polygon vertices are resolved at startup via Google Geocoding API.
 * The intersection address strings come from application.yml (bristol.zones.centro.*),
 * so they can be updated without recompiling if Google misreads any intersection.
 */
@Component
@RequiredArgsConstructor
public class MarDelPlataZoneDetector {


    private final AddressGeolocator geolocator;

    @Value("${bristol.zones.centro.vertex-nw}")
    private String vertexNw;

    @Value("${bristol.zones.centro.vertex-ne}")
    private String vertexNe;

    @Value("${bristol.zones.centro.vertex-se}")
    private String vertexSe;

    @Value("${bristol.zones.centro.vertex-sw}")
    private String vertexSw;

    // Approximate bounding box for Mar del Plata (sanity check)
    private static final double MDP_NORTH = -37.90;
    private static final double MDP_SOUTH = -38.15;
    private static final double MDP_WEST  = -57.80;
    private static final double MDP_EAST  = -57.48;

    private double[][] centro   = null;
    private double     latNorth;
    private double     latSouth;
    private double     latMid;
    private boolean    polygonReady = false;

    @EventListener(ContextRefreshedEvent.class)
    void resolvePolygon() {
        if (polygonReady) return;

        String[] addresses = { vertexNw, vertexNe, vertexSe, vertexSw };
        double[][] resolved = new double[4][2];

        for (int i = 0; i < addresses.length; i++) {
            Optional<Coordinates> coords = geolocator.geolocate(addresses[i]);
            if (coords.isEmpty()) {
                return;
            }
            resolved[i][0] = coords.get().lat();
            resolved[i][1] = coords.get().lng();
        }

        centro       = resolved;
        latNorth     = maxLat(resolved);
        latSouth     = minLat(resolved);
        latMid       = (latNorth + latSouth) / 2.0;
        polygonReady = true;
    }

    public Optional<DeliveryZoneType> detect(Coordinates coords) {
        if (!polygonReady || !isInMarDelPlata(coords)) {
            return Optional.empty();
        }

        if (isInsidePolygon(coords.lat(), coords.lng(), centro)) {
            // Even if the ray-casting says CENTRO, verify the point is actually
            // on the north side of the southern boundary (JBJ/Paso line: P3→P2).
            // Diagonal streets create a wedge where points physically south of the
            // boundary can still fall inside the polygon near the western corner.
            if (isSouthOfLine(coords.lat(), coords.lng(), centro[3], centro[2])) {
                return Optional.of(DeliveryZoneType.SUR);
            }
            return Optional.of(DeliveryZoneType.CENTRO);
        }

        if (coords.lat() > latNorth) return Optional.of(DeliveryZoneType.NORTE);
        if (coords.lat() < latSouth) return Optional.of(DeliveryZoneType.SUR);

        return Optional.of(coords.lat() >= latMid
                ? DeliveryZoneType.NORTE
                : DeliveryZoneType.SUR);
    }

    /**
     * Returns true if the point is on the SOUTH side of the directed line A→B.
     *
     * Uses the 2D cross product: (B-A) × (P-A).
     * In geographic coords (lat increases north, lng increases east):
     *   negative cross = point is to the RIGHT of A→B
     *
     * For the southern boundary A=SW corner → B=SE corner (going east-and-south),
     * "south of the line" = right side = cross < 0.
     */
    private boolean isSouthOfLine(double lat, double lng, double[] a, double[] b) {
        double cross = (b[1] - a[1]) * (lat - a[0]) - (b[0] - a[0]) * (lng - a[1]);
        return cross < 0;
    }

    private boolean isInsidePolygon(double lat, double lng, double[][] polygon) {
        boolean inside = false;
        int n = polygon.length;
        int j = n - 1;
        for (int i = 0; i < n; i++) {
            double latI = polygon[i][0], lngI = polygon[i][1];
            double latJ = polygon[j][0], lngJ = polygon[j][1];
            if (((latI > lat) != (latJ > lat)) &&
                (lng < (lngJ - lngI) * (lat - latI) / (latJ - latI) + lngI)) {
                inside = !inside;
            }
            j = i;
        }
        return inside;
    }

    private boolean isInMarDelPlata(Coordinates c) {
        return c.lat() >= MDP_SOUTH && c.lat() <= MDP_NORTH
                && c.lng() >= MDP_WEST  && c.lng() <= MDP_EAST;
    }

    private static double maxLat(double[][] p) {
        double m = p[0][0]; for (double[] v : p) if (v[0] > m) m = v[0]; return m;
    }

    private static double minLat(double[][] p) {
        double m = p[0][0]; for (double[] v : p) if (v[0] < m) m = v[0]; return m;
    }
}
