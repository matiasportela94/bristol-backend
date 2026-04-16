package com.bristol.application.address.usecase;

import com.bristol.application.address.dto.UpdateUserAddressRequest;
import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserAddressUseCase {
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper userAddressMapper;
    private final TimeProvider timeProvider;

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
                timeProvider.now()
        );

        UserAddress saved = userAddressRepository.save(updated);
        return userAddressMapper.toDto(saved);
    }
}
