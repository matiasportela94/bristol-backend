package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.application.distributor.dto.UploadedFileDto;
import com.bristol.domain.distributor.Distributor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistributorMapper {
    public DistributorDto toDto(Distributor distributor) {
        return toDto(distributor, null, List.of());
    }

    public DistributorDto toDto(Distributor distributor, List<UploadedFileDto> uploadedFiles) {
        return toDto(distributor, null, uploadedFiles);
    }

    public DistributorDto toDto(Distributor distributor, String userEmail, List<UploadedFileDto> uploadedFiles) {
        return DistributorDto.builder()
                .id(distributor.getId().getValue().toString())
                .userId(distributor.getUserId().getValue().toString())
                .userEmail(userEmail)
                .address(distributor.getAddress())
                .phone(distributor.getPhone())
                .cuit(distributor.getCuit())
                .razonSocial(distributor.getRazonSocial())
                .status(distributor.getStatus())
                .totalOrders(distributor.getTotalOrders())
                .totalSpent(distributor.getTotalSpent())
                .totalBeers(distributor.getTotalBeers())
                .totalProfit(distributor.getTotalProfit())
                .createdAt(distributor.getCreatedAt())
                .updatedAt(distributor.getUpdatedAt())
                .uploadedFiles(uploadedFiles)
                .build();
    }
}
