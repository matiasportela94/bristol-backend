package com.bristol.application.catalog.merchtype.usecase;

import com.bristol.application.catalog.merchtype.dto.MerchTypeDto;
import com.bristol.domain.catalog.MerchType;
import org.springframework.stereotype.Component;

/**
 * Mapper between MerchType domain and DTO.
 */
@Component
public class MerchTypeApplicationMapper {

    public MerchTypeDto toDto(MerchType merchType) {
        if (merchType == null) {
            return null;
        }

        return MerchTypeDto.builder()
                .id(merchType.getId().getValue().toString())
                .code(merchType.getCode())
                .name(merchType.getName())
                .description(merchType.getDescription())
                .category(merchType.getCategory())
                .active(merchType.isActive())
                .displayOrder(merchType.getDisplayOrder())
                .createdAt(merchType.getCreatedAt())
                .updatedAt(merchType.getUpdatedAt())
                .build();
    }
}
