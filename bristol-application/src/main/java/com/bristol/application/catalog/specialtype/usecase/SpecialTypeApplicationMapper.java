package com.bristol.application.catalog.specialtype.usecase;

import com.bristol.application.catalog.specialtype.dto.SpecialTypeDto;
import com.bristol.domain.catalog.SpecialType;
import org.springframework.stereotype.Component;

/**
 * Mapper between SpecialType domain and DTO.
 */
@Component
public class SpecialTypeApplicationMapper {

    public SpecialTypeDto toDto(SpecialType specialType) {
        if (specialType == null) {
            return null;
        }

        return SpecialTypeDto.builder()
                .id(specialType.getId().getValue().toString())
                .code(specialType.getCode())
                .name(specialType.getName())
                .description(specialType.getDescription())
                .requiresQuote(specialType.isRequiresQuote())
                .active(specialType.isActive())
                .displayOrder(specialType.getDisplayOrder())
                .createdAt(specialType.getCreatedAt())
                .updatedAt(specialType.getUpdatedAt())
                .build();
    }
}
