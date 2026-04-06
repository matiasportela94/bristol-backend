package com.bristol.application.address.usecase;

import com.bristol.application.address.dto.UpdateUserAddressRequest;
import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateUserAddressUseCase {
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper userAddressMapper;

    @Transactional
    public UserAddressDto execute(String addressId, UpdateUserAddressRequest request) {
        UserAddressId id = new UserAddressId(addressId);
        DeliveryZoneId zoneId = new DeliveryZoneId(request.getDeliveryZoneId());

        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Address not found: " + addressId));

        UserAddress updated = address.update(
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getCity(),
                request.getProvince(),
                request.getPostalCode(),
                zoneId,
                Instant.now()
        );

        UserAddress saved = userAddressRepository.save(updated);
        return userAddressMapper.toDto(saved);
    }
}
