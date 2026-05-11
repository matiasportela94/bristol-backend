package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.application.catalog.beerstyle.dto.CreateBeerStyleRequest;
import com.bristol.domain.catalog.BeerStyle;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to create a new beer style.
 */
@Service
@RequiredArgsConstructor
public class CreateBeerStyleUseCase {

    private final BeerStyleRepository beerStyleRepository;
    private final BeerStyleApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    public BeerStyleDto execute(CreateBeerStyleRequest request) {
        // Validate code uniqueness
        if (beerStyleRepository.existsByCode(request.getCode())) {
            throw new ValidationException("Beer style with code '" + request.getCode() + "' already exists");
        }

        // Create beer style
        BeerStyle beerStyle = BeerStyle.create(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                timeProvider.now()
        );

        // Save and return
        BeerStyle saved = beerStyleRepository.save(beerStyle);
        return mapper.toDto(saved);
    }
}
