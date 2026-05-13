package com.bristol.infrastructure.geo;

import com.bristol.application.geo.AddressGeolocator;
import com.bristol.domain.shared.valueobject.Coordinates;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAddressGeolocator implements AddressGeolocator {

    private static final Logger log = LoggerFactory.getLogger(GoogleAddressGeolocator.class);
    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key:}")
    private String apiKey;

    @Override
    public Optional<Coordinates> geolocate(String address) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Google Maps API key not configured — skipping geocoding");
            return Optional.empty();
        }

        try {
            String fullAddress = address + ", Mar del Plata, Buenos Aires, Argentina";
            URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                    .queryParam("address", fullAddress)
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode results = root.get("results");

            if (results == null || results.isEmpty()) {
                log.debug("Geocoding returned no results for address: {}", address);
                return Optional.empty();
            }

            JsonNode geometry    = results.get(0).get("geometry");
            JsonNode location    = geometry.get("location");
            String locationType  = geometry.path("location_type").asText("UNKNOWN");

            double lat = location.get("lat").asDouble();
            double lng = location.get("lng").asDouble();
            log.debug("Geocoding '{}' → lat={}, lng={} [{}]", address, lat, lng, locationType);

            // APPROXIMATE results are too imprecise for zone boundary detection.
            // Return empty so the caller falls back to manual zone selection.
            if ("APPROXIMATE".equals(locationType)) {
                log.warn("Geocoding '{}' returned APPROXIMATE precision — rejecting for zone detection", address);
                return Optional.empty();
            }

            return Optional.of(new Coordinates(lat, lng));

        } catch (Exception e) {
            log.error("Geocoding failed for address '{}': {}", address, e.getMessage());
            return Optional.empty();
        }
    }
}
