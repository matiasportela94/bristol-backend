package com.bristol.application.address.usecase;

import com.bristol.application.address.dto.CreateUserAddressRequest;
import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.domain.address.UserAddress;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.delivery.DeliveryZoneId;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserAddressUseCase {
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper userAddressMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public UserAddressDto execute(CreateUserAddressRequest request) {
        UserId userId = new UserId(request.getUserId());
        DeliveryZoneId zoneId = new DeliveryZoneId(request.getDeliveryZoneId());

        UserAddress address = UserAddress.create(
                userId,
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getCity(),
                request.getProvince(),
                request.getPostalCode(),
                zoneId,
                request.isDefault(),
                timeProvider.now()
        );

        UserAddress saved = userAddressRepository.save(address);
        return userAddressMapper.toDto(saved);
    }
}
