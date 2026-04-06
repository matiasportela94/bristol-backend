package com.bristol.application.address.usecase;

import com.bristol.application.address.dto.UserAddressDto;
import com.bristol.domain.address.UserAddressRepository;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserAddressesUseCase {
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper userAddressMapper;

    @Transactional(readOnly = true)
    public List<UserAddressDto> execute(String userId) {
        UserId id = new UserId(userId);
        return userAddressRepository.findByUserId(id).stream()
                .map(userAddressMapper::toDto)
                .collect(Collectors.toList());
    }
}
