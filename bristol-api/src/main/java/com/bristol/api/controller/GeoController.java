package com.bristol.api.controller;

import com.bristol.application.geo.AddressSuggestionDto;
import com.bristol.infrastructure.geo.GoogleAddressAutocomplete;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
@Tag(name = "Geo", description = "Geographic utilities")
public class GeoController {

    private final GoogleAddressAutocomplete autocomplete;

    @GetMapping("/autocomplete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRIBUTOR', 'DISTRIBUTOR_BRANCH')")
    @Operation(summary = "Address autocomplete", description = "Returns street address suggestions for Mar del Plata")
    public ResponseEntity<List<AddressSuggestionDto>> autocomplete(
            @RequestParam String input
    ) {
        return ResponseEntity.ok(autocomplete.suggest(input));
    }
}
