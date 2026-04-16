package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.DistributorDto;
import com.bristol.application.distributor.dto.UpdateDistributorRequest;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * Use case to update distributor information.
 */
@Service
@RequiredArgsConstructor
public class UpdateDistributorUseCase {

    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final DistributorMapper distributorMapper;
    private final DistributorDocumentQueryService distributorDocumentQueryService;
    private final TimeProvider timeProvider;

    @Transactional
    public DistributorDto execute(String id, UpdateDistributorRequest request) {
        DistributorId distributorId = new DistributorId(UUID.fromString(id));
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new NotFoundException("Distributor", id));

        DeliveryZoneId deliveryZoneId = new DeliveryZoneId(UUID.fromString(request.getDeliveryZoneId()));

        Distributor updatedDistributor = distributor.updateBusinessInfo(
                request.getAddress(),
                request.getPhone(),
                request.getCuit(),
                request.getRazonSocial(),
                deliveryZoneId,
                timeProvider.now()
        );

        Distributor savedDistributor = distributorRepository.save(updatedDistributor);
        String userEmail = userRepository.findById(savedDistributor.getUserId())
                .map(user -> user.getEmail())
                .orElse(null);
        return distributorMapper.toDto(
                savedDistributor,
                userEmail,
                distributorDocumentQueryService.getDocuments(savedDistributor)
        );
    }
}
