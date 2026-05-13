package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.application.distributorbranch.dto.UpdateDistributorBranchRequest;
import com.bristol.application.geo.AddressGeolocator;
import com.bristol.application.geo.MarDelPlataZoneDetector;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.distributor.DistributorBranch;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDistributorBranchUseCase {

    private final DistributorBranchRepository branchRepository;
    private final DistributorBranchMapper mapper;
    private final AddressGeolocator geolocator;
    private final MarDelPlataZoneDetector zoneDetector;
    private final TimeProvider timeProvider;

    @Transactional
    public DistributorBranchDto execute(String branchId, UpdateDistributorBranchRequest request) {
        DistributorBranch branch = branchRepository.findById(new DistributorBranchId(branchId))
                .orElseThrow(() -> new NotFoundException("Branch", branchId));

        DeliveryZoneId deliveryZoneId = resolveZone(request, branch);

        DistributorBranch updated = branch.update(
                request.getName(),
                request.getAddress(),
                request.getCity(),
                request.getProvince(),
                request.getCodigoPostal(),
                deliveryZoneId,
                timeProvider.now()
        );

        return mapper.toDto(branchRepository.save(updated));
    }

    private DeliveryZoneId resolveZone(UpdateDistributorBranchRequest request, DistributorBranch existing) {
        // Manual override always wins
        if (request.getDeliveryZone() != null && !request.getDeliveryZone().isBlank()) {
            return DeliveryZoneType.fromString(request.getDeliveryZone()).getDeliveryZoneId();
        }

        // Use the address from the request, falling back to the existing one
        String address = hasText(request.getAddress()) ? request.getAddress() : existing.getAddress();
        String city    = hasText(request.getCity())    ? request.getCity()    : existing.getCity();
        String province= hasText(request.getProvince())? request.getProvince(): existing.getProvince();
        String cp      = hasText(request.getCodigoPostal()) ? request.getCodigoPostal() : existing.getCodigoPostal();

        // Only the street — the geolocator appends ", Mar del Plata, Buenos Aires, Argentina"
        return geolocator.geolocate(address != null ? address : "")
                .flatMap(zoneDetector::detect)
                .map(DeliveryZoneType::getDeliveryZoneId)
                .orElse(existing.getDeliveryZoneId()); // keep existing zone if geocoding fails
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
