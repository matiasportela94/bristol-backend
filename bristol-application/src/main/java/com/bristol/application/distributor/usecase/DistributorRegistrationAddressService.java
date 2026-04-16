package com.bristol.application.distributor.usecase;

import com.bristol.application.distributor.dto.CreateDistributorRegistrationRequest;
import com.bristol.application.distributor.dto.RegistrationShippingAddressDto;
import com.bristol.application.distributor.dto.RegistrationShippingAddressPayload;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.distributor.DistributorRegistrationAddress;
import com.bristol.domain.distributor.DistributorRegistrationAddressRepository;
import com.bristol.domain.distributor.DistributorRegistrationRequest;
import com.bristol.domain.distributor.DistributorRegistrationRequestId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistributorRegistrationAddressService {

    private final DistributorRegistrationAddressRepository registrationAddressRepository;
    private final UserAddressRepository userAddressRepository;
    private final TimeProvider timeProvider;

    public void createAddresses(DistributorRegistrationRequest registration, CreateDistributorRegistrationRequest request) {
        List<RegistrationShippingAddressPayload> shippingAddresses = normalizeShippingAddresses(request);
        Instant now = timeProvider.now();

        shippingAddresses.forEach(address -> registrationAddressRepository.save(
                DistributorRegistrationAddress.create(
                        registration.getId(),
                        address.getAddressLine1(),
                        address.getAddressLine2(),
                        address.getCity(),
                        address.getProvince(),
                        address.getPostalCode(),
                        resolveDeliveryZone(address.getDeliveryZone(), request.getDeliveryZone()),
                        address.isDefault(),
                        now
                )
        ));
    }

    public List<RegistrationShippingAddressDto> toDtos(DistributorRegistrationRequestId registrationRequestId) {
        return registrationAddressRepository.findByRegistrationRequestId(registrationRequestId).stream()
                .map(this::toDto)
                .toList();
    }

    public void assignAddressesToUser(DistributorRegistrationRequestId registrationRequestId, User user) {
        List<UserAddress> existingAddresses = userAddressRepository.findByUserId(user.getId());

        for (DistributorRegistrationAddress address : registrationAddressRepository.findByRegistrationRequestId(registrationRequestId)) {
            boolean alreadyExists = existingAddresses.stream().anyMatch(existing ->
                    sameValue(existing.getAddressLine1(), address.getAddressLine1())
                            && sameValue(existing.getAddressLine2(), address.getAddressLine2())
                            && sameValue(existing.getCity(), address.getCity())
                            && sameValue(existing.getProvince(), address.getProvince())
                            && sameValue(existing.getPostalCode(), address.getPostalCode())
                            && existing.getDeliveryZoneId().equals(address.getDeliveryZoneId()));

            if (alreadyExists) {
                continue;
            }

            UserAddress userAddress = UserAddress.create(
                    user.getId(),
                    address.getAddressLine1(),
                    address.getAddressLine2(),
                    address.getCity(),
                    address.getProvince(),
                    address.getPostalCode(),
                    address.getDeliveryZoneId(),
                    address.isDefault(),
                    timeProvider.now()
            );
            userAddressRepository.save(userAddress);
        }
    }

    public Optional<DistributorRegistrationAddress> getDefaultAddress(DistributorRegistrationRequestId registrationRequestId) {
        List<DistributorRegistrationAddress> addresses = registrationAddressRepository.findByRegistrationRequestId(registrationRequestId);
        return addresses.stream()
                .filter(DistributorRegistrationAddress::isDefault)
                .findFirst()
                .or(() -> addresses.stream().findFirst());
    }

    private List<RegistrationShippingAddressPayload> normalizeShippingAddresses(CreateDistributorRegistrationRequest request) {
        if (request.getShippingAddresses() != null && !request.getShippingAddresses().isEmpty()) {
            return request.getShippingAddresses();
        }

        boolean hasSpecificShippingAddress =
                hasText(request.getProvinciaEnvio())
                        || hasText(request.getCiudadEnvio())
                        || hasText(request.getDireccionEnvio())
                        || hasText(request.getCodigoPostalEnvio());

        RegistrationShippingAddressPayload payload = RegistrationShippingAddressPayload.builder()
                .addressLine1(hasSpecificShippingAddress ? request.getDireccionEnvio() : request.getDireccion())
                .city(hasSpecificShippingAddress ? request.getCiudadEnvio() : request.getCiudad())
                .province(hasSpecificShippingAddress ? request.getProvinciaEnvio() : request.getProvincia())
                .postalCode(hasSpecificShippingAddress ? request.getCodigoPostalEnvio() : request.getCodigoPostal())
                .deliveryZone(request.getDeliveryZone())
                .isDefault(true)
                .build();

        validateNormalizedPayload(payload);
        return List.of(payload);
    }

    private void validateNormalizedPayload(RegistrationShippingAddressPayload payload) {
        if (!hasText(payload.getAddressLine1()) || !hasText(payload.getCity()) || !hasText(payload.getProvince())) {
            throw new ValidationException("Shipping address, city and province are required");
        }
    }

    private DeliveryZoneId resolveDeliveryZone(String addressDeliveryZone, String fallbackDeliveryZone) {
        return DeliveryZoneType.fromString(hasText(addressDeliveryZone) ? addressDeliveryZone : fallbackDeliveryZone)
                .getDeliveryZoneId();
    }

    private RegistrationShippingAddressDto toDto(DistributorRegistrationAddress address) {
        return RegistrationShippingAddressDto.builder()
                .id(address.getId().getValue().toString())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryZoneId(address.getDeliveryZoneId().getValue().toString())
                .deliveryZone(mapDeliveryZoneIdToName(address.getDeliveryZoneId()))
                .isDefault(address.isDefault())
                .build();
    }

    private String mapDeliveryZoneIdToName(DeliveryZoneId deliveryZoneId) {
        return Arrays.stream(DeliveryZoneType.values())
                .filter(type -> type.getUuid().equals(deliveryZoneId.getValue()))
                .findFirst()
                .map(type -> type.name().toLowerCase())
                .orElse("centro");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean sameValue(String left, String right) {
        String normalizedLeft = left == null ? "" : left.trim();
        String normalizedRight = right == null ? "" : right.trim();
        return normalizedLeft.equalsIgnoreCase(normalizedRight);
    }
}
