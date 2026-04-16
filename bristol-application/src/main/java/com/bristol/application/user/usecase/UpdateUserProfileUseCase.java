package com.bristol.application.user.usecase;

import com.bristol.application.user.dto.UpdateUserProfileRequest;
import com.bristol.application.user.dto.UserDto;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Use case to update user profile.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TimeProvider timeProvider;

    @Transactional
    public UserDto execute(String userId, UpdateUserProfileRequest request) {
        UserId id = new UserId(userId);
        var now = timeProvider.now();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found: " + userId));

        User updatedUser = user.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                now
        );

        User savedUser = userRepository.save(updatedUser);
        return userMapper.toDto(savedUser);
    }
}
