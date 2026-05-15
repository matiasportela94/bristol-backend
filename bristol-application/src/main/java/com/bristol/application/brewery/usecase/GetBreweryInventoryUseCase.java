package com.bristol.application.brewery.usecase;

import com.bristol.application.brewery.dto.BreweryInventoryDto;
import com.bristol.domain.brewery.BreweryInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBreweryInventoryUseCase {

    private final BreweryInventoryRepository inventoryRepository;
    private final BreweryApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<BreweryInventoryDto> execute() {
        return inventoryRepository.findAll().stream().map(mapper::toDto).toList();
    }
}
