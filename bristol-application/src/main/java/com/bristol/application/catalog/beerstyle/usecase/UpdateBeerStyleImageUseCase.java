package com.bristol.application.catalog.beerstyle.usecase;

import com.bristol.application.catalog.beerstyle.dto.BeerStyleDto;
import com.bristol.domain.catalog.BeerStyleId;
import com.bristol.domain.catalog.BeerStyleRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateBeerStyleImageUseCase {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final BeerStyleRepository beerStyleRepository;
    private final BeerStyleApplicationMapper mapper;
    private final TimeProvider timeProvider;

    @Transactional
    @CacheEvict(value = "beerStyles", allEntries = true)
    public BeerStyleDto execute(UUID id, byte[] imageData, String contentType, String fileName) {
        if (imageData == null || imageData.length == 0) {
            throw new ValidationException("Image data is required");
        }
        if (imageData.length > MAX_SIZE_BYTES) {
            throw new ValidationException("Image size cannot exceed 5 MB");
        }
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("File must be an image");
        }

        var style = beerStyleRepository.findById(new BeerStyleId(id))
                .orElseThrow(() -> new NotFoundException("BeerStyle", id.toString()));

        return mapper.toDto(beerStyleRepository.save(
                style.updateImage(imageData, contentType, fileName, timeProvider.now())
        ));
    }
}
