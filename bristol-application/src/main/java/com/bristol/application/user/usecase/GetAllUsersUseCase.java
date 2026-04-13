package com.bristol.application.user.usecase;

import com.bristol.application.user.dto.UserDto;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> execute() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }
}
