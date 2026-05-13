package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.CreateDistributorBranchRequest;
import com.bristol.application.distributorbranch.dto.DistributorBranchDto;
import com.bristol.application.geo.AddressGeolocator;
import com.bristol.application.geo.MarDelPlataZoneDetector;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorBranch;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDistributorBranchUseCase {

    private final DistributorBranchRepository branchRepository;
    private final DistributorRepository distributorRepository;
    private final DistributorBranchMapper mapper;
    private final AddressGeolocator geolocator;
    private final MarDelPlataZoneDetector zoneDetector;
    private final TimeProvider timeProvider;

    @Transactional
    public DistributorBranchDto execute(String distributorId, CreateDistributorBranchRequest request) {
        Distributor distributor = distributorRepository.findById(new DistributorId(distributorId))
                .orElseThrow(() -> new NotFoundException("Distributor", distributorId));

        DeliveryZoneId deliveryZoneId = resolveZone(request);

        DistributorBranch branch = DistributorBranch.create(
                distributor.getId(),
                request.getName(),
                request.getAddress(),
                request.getCity(),
                request.getProvince(),
                request.getCodigoPostal(),
                deliveryZoneId,
                timeProvider.now()
        );

        return mapper.toDto(branchRepository.save(branch));
    }

    private DeliveryZoneId resolveZone(CreateDistributorBranchRequest request) {
        if (request.getDeliveryZone() != null && !request.getDeliveryZone().isBlank()) {
            return DeliveryZoneType.fromString(request.getDeliveryZone()).getDeliveryZoneId();
        }

        String addressForGeocoding = buildAddress(request);
        return geolocator.geolocate(addressForGeocoding)
                .flatMap(zoneDetector::detect)
                .map(DeliveryZoneType::getDeliveryZoneId)
                .orElseThrow(() -> new ValidationException(
                        "No se pudo determinar la zona de entrega automáticamente para la dirección indicada. " +
                        "Por favor especificá la zona manualmente (sur, norte o centro)."
                ));
    }

    private String buildAddress(CreateDistributorBranchRequest req) {
        // Only the street — the geolocator appends ", Mar del Plata, Buenos Aires, Argentina"
        return req.getAddress() != null ? req.getAddress() : "";
    }
}
