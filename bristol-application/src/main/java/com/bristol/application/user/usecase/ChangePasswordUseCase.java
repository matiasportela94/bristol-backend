package com.bristol.application.user.usecase;

import com.bristol.application.user.dto.ChangePasswordRequest;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Use case to change user password.
 */
@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TimeProvider timeProvider;

    @Transactional
    public void execute(String userId, ChangePasswordRequest request) {
        UserId id = new UserId(userId);
        var now = timeProvider.now();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("User not found: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Hash new password
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        // Update password
        User updatedUser = user.changePassword(newPasswordHash, now);
        userRepository.save(updatedUser);
    }
}
