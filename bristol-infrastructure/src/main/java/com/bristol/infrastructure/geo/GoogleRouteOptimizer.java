package com.bristol.infrastructure.geo;

import com.bristol.application.delivery.route.RouteOptimizer;
import com.bristol.application.delivery.route.RouteOptimizerResult;
import com.bristol.domain.shared.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GoogleRouteOptimizer implements RouteOptimizer {

    private static final Logger log = LoggerFactory.getLogger(GoogleRouteOptimizer.class);
    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";

    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key:}")
    private String apiKey;

    @Override
    public RouteOptimizerResult optimize(String depotAddress, List<String> waypointAddresses) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ValidationException("Google Maps API key no configurada. " +
                    "Configurá GOOGLE_MAPS_API_KEY en las variables de entorno.");
        }
        if (waypointAddresses.isEmpty()) {
            return RouteOptimizerResult.builder()
                    .waypointOrder(List.of())
                    .legDurationsSeconds(List.of())
                    .legDistancesMeters(List.of())
                    .build();
        }

        try {
            String waypoints = "optimize:true|" + String.join("|", waypointAddresses);
            URI uri = UriComponentsBuilder.fromHttpUrl(DIRECTIONS_URL)
                    .queryParam("origin", depotAddress)
                    .queryParam("destination", depotAddress)
                    .queryParam("waypoints", waypoints)
                    .queryParam("mode", "driving")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            String status = root.get("status").asText();

            if (!"OK".equals(status)) {
                log.error("Directions API returned status: {} — {}", status,
                        root.path("error_message").asText(""));
                throw new ValidationException(
                        "No se pudo calcular el recorrido. Estado de la API: " + status);
            }

            JsonNode route = root.get("routes").get(0);

            // Optimized waypoint order (indices into the original waypointAddresses list)
            List<Integer> waypointOrder = StreamSupport
                    .stream(route.get("waypoint_order").spliterator(), false)
                    .map(JsonNode::asInt)
                    .toList();

            // Per-leg durations and distances (legs include depot→first and last→depot)
            List<Integer> durations = new ArrayList<>();
            List<Integer> distances = new ArrayList<>();
            for (JsonNode leg : route.get("legs")) {
                durations.add(leg.get("duration").get("value").asInt());
                distances.add(leg.get("distance").get("value").asInt());
            }

            return RouteOptimizerResult.builder()
                    .waypointOrder(waypointOrder)
                    .legDurationsSeconds(durations)
                    .legDistancesMeters(distances)
                    .build();

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling Directions API: {}", e.getMessage(), e);
            throw new ValidationException("Error al calcular el recorrido: " + e.getMessage());
        }
    }
}
