package com.bristol.domain.user;

import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * User domain entity.
 * Represents a user in the system (can be a regular user, distributor, or admin).
 */
@Getter
@Builder(toBuilder = true)
public class User {

    private final UserId id;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final UserRole role;
    private final boolean isDistributor;
    /** Set when role is DISTRIBUTOR or DISTRIBUTOR_BRANCH. */
    private final DistributorId distributorId;
    /** Set when role is DISTRIBUTOR_BRANCH. */
    private final DistributorBranchId branchId;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Factory method to create a new user.
     */
    public static User create(
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            UserRole role,
            Instant now
    ) {
        return User.builder()
                .id(UserId.generate())
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .role(role != null ? role : UserRole.USER)
                .isDistributor(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Update user profile (first name and last name).
     */
    public User updateProfile(String firstName, String lastName, Instant now) {
        return this.toBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .updatedAt(now)
                .build();
    }

    /**
     * Change user password.
     */
    public User changePassword(String newPasswordHash, Instant now) {
        return this.toBuilder()
                .passwordHash(newPasswordHash)
                .updatedAt(now)
                .build();
    }

    /**
     * Check if user is an administrator.
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
