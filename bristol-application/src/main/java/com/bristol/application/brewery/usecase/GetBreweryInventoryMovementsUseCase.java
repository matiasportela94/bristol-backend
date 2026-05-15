package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.BreweryInventoryMovementDto;
import com.bristol.domain.brewery.BreweryInventoryMovement;
import com.bristol.domain.brewery.BreweryInventoryMovementRepository;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBreweryInventoryMovementsUseCase {

    private final BreweryInventoryMovementRepository movementRepository;
    private final BeerStyleRepository beerStyleRepository;

    @Transactional(readOnly = true)
    public List<BreweryInventoryMovementDto> executeByStyle(UUID beerStyleId) {
        BeerStyleId styleId = new BeerStyleId(beerStyleId);
        BeerStyle style = beerStyleRepository.findById(styleId).orElse(null);
        return movementRepository.findByBeerStyleId(styleId).stream()
                .map(m -> toDto(m, style))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BreweryInventoryMovementDto> executeAll() {
        Map<String, BeerStyle> stylesById = beerStyleRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getId().asString(), s -> s));
        return movementRepository.findAll().stream()
                .map(m -> toDto(m, stylesById.get(m.getBeerStyleId().asString())))
                .toList();
    }

    private BreweryInventoryMovementDto toDto(BreweryInventoryMovement m, BeerStyle style) {
        return BreweryInventoryMovementDto.builder()
                .id(m.getId().asString())
                .beerStyleId(m.getBeerStyleId().asString())
                .beerStyleName(style != null ? style.getName() : null)
                .beerStyleCode(style != null ? style.getCode() : null)
                .type(m.getType())
                .cansDelta(m.getCansDelta())
                .cansBefore(m.getCansBefore())
                .cansAfter(m.getCansAfter())
                .referenceId(m.getReferenceId())
                .referenceType(m.getReferenceType())
                .notes(m.getNotes())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
