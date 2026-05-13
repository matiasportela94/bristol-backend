package com.bristol.infrastructure.geo;

import com.bristol.application.geo.AddressSuggestionDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls the Places API (New) autocomplete endpoint.
 * POST https://places.googleapis.com/v1/places:autocomplete
 */
@Service
@RequiredArgsConstructor
public class GoogleAddressAutocomplete {

    private static final Logger log = LoggerFactory.getLogger(GoogleAddressAutocomplete.class);
    private static final String AUTOCOMPLETE_URL = "https://places.googleapis.com/v1/places:autocomplete";

    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key:}")
    private String apiKey;

    public List<AddressSuggestionDto> suggest(String input) {
        if (apiKey == null || apiKey.isBlank() || input == null || input.isBlank()) {
            return List.of();
        }

        try {
            String body = objectMapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
                put("input", input);
                put("languageCode", "es");
                put("includedRegionCodes", List.of("ar"));
                put("locationBias", new java.util.LinkedHashMap<String, Object>() {{
                    put("circle", new java.util.LinkedHashMap<String, Object>() {{
                        put("center", new java.util.LinkedHashMap<String, Object>() {{
                            put("latitude", -38.0);
                            put("longitude", -57.55);
                        }});
                        put("radius", 20000.0);
                    }});
                }});
            }});

            HttpRequest request = HttpRequest.newBuilder(URI.create(AUTOCOMPLETE_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "suggestions.placePrediction.text,suggestions.placePrediction.structuredFormat,suggestions.placePrediction.placeId")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            if (root.has("error")) {
                log.debug("Places Autocomplete error: {}", root.path("error").path("message").asText());
                return List.of();
            }

            List<AddressSuggestionDto> results = new ArrayList<>();
            for (JsonNode suggestion : root.path("suggestions")) {
                JsonNode pp = suggestion.path("placePrediction");
                JsonNode sf = pp.path("structuredFormat");
                results.add(AddressSuggestionDto.builder()
                        .description(pp.path("text").path("text").asText())
                        .mainText(sf.path("mainText").path("text").asText())
                        .secondaryText(sf.path("secondaryText").path("text").asText())
                        .placeId(pp.path("placeId").asText())
                        .build());
            }
            return results;

        } catch (Exception e) {
            log.error("Places Autocomplete failed for '{}': {}", input, e.getMessage());
            return List.of();
        }
    }
}
