package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.CreateDistributorRequest;
import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateDistributorUseCase {
    private final DistributorRepository distributorRepository;
    private final DistributorMapper distributorMapper;

    @Transactional
    public DistributorDto execute(CreateDistributorRequest request) {
        UserId userId = new UserId(request.getUserId());
        DeliveryZoneId zoneId = new DeliveryZoneId(request.getDeliveryZoneId());

        Distributor distributor = Distributor.create(
                userId,
                request.getAddress(),
                request.getPhone(),
                request.getDni(),
                request.getCuit(),
                request.getRazonSocial(),
                request.getDateOfBirth(),
                zoneId,
                Instant.now()
        );

        Distributor saved = distributorRepository.save(distributor);
        return distributorMapper.toDto(saved);
    }
}
