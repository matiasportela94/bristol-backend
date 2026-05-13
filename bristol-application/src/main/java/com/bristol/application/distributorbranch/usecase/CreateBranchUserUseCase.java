package com.bristol.application.distributorbranch.usecase;

import com.bristol.application.distributorbranch.dto.AssignBranchUserRequest;
import com.bristol.application.user.dto.UserDto;
import com.bristol.application.user.usecase.UserMapper;
import com.bristol.domain.distributor.DistributorBranch;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorBranchRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a new user linked to a specific distributor branch.
 * The user gets role DISTRIBUTOR_BRANCH and can only see their branch's data.
 */
@Service
@RequiredArgsConstructor
public class CreateBranchUserUseCase {

    private final DistributorBranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public UserDto execute(String branchId, AssignBranchUserRequest request) {
        DistributorBranch branch = branchRepository.findById(new DistributorBranchId(branchId))
                .orElseThrow(() -> new NotFoundException("Branch", branchId));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .id(com.bristol.domain.user.UserId.generate())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.DISTRIBUTOR_BRANCH)
                .isDistributor(true)
                .distributorId(branch.getDistributorId())
                .branchId(branch.getId())
                .createdAt(timeProvider.now())
                .updatedAt(timeProvider.now())
                .build();

        return userMapper.toDto(userRepository.save(user));
    }
}
