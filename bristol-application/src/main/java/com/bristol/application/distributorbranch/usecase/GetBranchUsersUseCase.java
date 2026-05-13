package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.user.dto.UserDto;
import com.bristol.application.user.usecase.UserMapper;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBranchUsersUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> execute(String branchId) {
        return userRepository.findByBranchId(new DistributorBranchId(branchId))
                .stream().map(userMapper::toDto).toList();
    }
}
