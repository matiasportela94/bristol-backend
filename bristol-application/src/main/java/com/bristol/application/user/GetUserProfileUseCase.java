package com.bristol.application.user;

import com.bristol.application.common.UseCase;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Use case: Get user profile by ID.
 */
@RequiredArgsConstructor
public class GetUserProfileUseCase implements UseCase<UserId, User> {

    private final UserRepository userRepository;

    @Override
    public User execute(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.asString()));
    }
}
