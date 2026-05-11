package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case to deactivate a beer style.
 */
@Service
@RequiredArgsConstructor
public class DeactivateBeerStyleUseCase {

    private final BeerStyleRepository beerStyleRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(UUID id) {
        // Find existing beer style
        BeerStyle beerStyle = beerStyleRepository.findById(new BeerStyleId(id))
                .orElseThrow(() -> new NotFoundException("BeerStyle", id.toString()));

        // Deactivate and save
        BeerStyle deactivated = beerStyle.deactivate(timeProvider.now());
        beerStyleRepository.save(deactivated);
    }
}
