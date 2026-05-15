package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.application.catalog.beerstyle.dto.UpdateBeerStyleRequest;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to update a beer style.
 */
@Service
@RequiredArgsConstructor
public class UpdateBeerStyleUseCase {

    private final BeerStyleRepository beerStyleRepository;
    private final BeerStyleApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    @CacheEvict(value = "beerStyles", allEntries = true)
    public BeerStyleDto execute(UUID id, UpdateBeerStyleRequest request) {
        // Find existing beer style
        BeerStyle beerStyle = beerStyleRepository.findById(new BeerStyleId(id))
                .orElseThrow(() -> new NotFoundException("BeerStyle", id.toString()));

        // Update
        BeerStyle updated = beerStyle.update(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getDisplayOrder(),
                request.getAbv(),
                request.getIbu(),
                request.getSrm(),
                timeProvider.now()
        );

        // Save and return
        BeerStyle saved = beerStyleRepository.save(updated);
        return mapper.toDto(saved);
    }
}
