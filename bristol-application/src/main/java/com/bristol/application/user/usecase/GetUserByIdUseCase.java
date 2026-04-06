package com.bristol.application.user.usecase;

import com.bristol.application.user.dto.UserDto;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to get user by ID.
 */
@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto execute(String userId) {
        UserId id = new UserId(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found: " + userId));
        return userMapper.toDto(user);
    }
}
