package com.bristol.application.address.usecase;

import com.bristol.domain.address.UserAddressId;
import com.bristol.domain.address.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserAddressUseCase {
    private final UserAddressRepository userAddressRepository;

    @Transactional
    public void execute(String addressId) {
        UserAddressId id = new UserAddressId(addressId);
        userAddressRepository.delete(id);
    }
}
