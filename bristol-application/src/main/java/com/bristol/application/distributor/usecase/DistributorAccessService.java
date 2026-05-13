package com.bristol.application.distributor.usecase;

import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.shared.exception.NotFoundException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributorAccessService {

    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;

    public Distributor getAccessibleDistributor(String distributorId, String userEmail, boolean isAdmin) {
        Distributor distributor = distributorRepository.findById(new DistributorId(distributorId))
                .orElseThrow(() -> new NotFoundException("Distributor", distributorId));

        if (isAdmin) {
            return distributor;
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));

        if (!canAccessDistributor(user, distributor)) {
            throw new AccessDeniedException("Distributor does not belong to the authenticated user");
        }

        return distributor;
    }

    public Distributor getAccessibleDistributorByUserId(String userId, String userEmail, boolean isAdmin) {
        Distributor distributor = distributorRepository.findByUserId(new UserId(userId))
                .orElseThrow(() -> new NotFoundException("Distributor for user", userId));

        if (isAdmin) {
            return distributor;
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));

        if (!canAccessDistributor(user, distributor)) {
            throw new AccessDeniedException("Distributor does not belong to the authenticated user");
        }

        return distributor;
    }

    public boolean isAdmin(java.util.Collection<?> authorities) {
        return authorities.stream().anyMatch(authority -> authority.toString().equals("ROLE_ADMIN"));
    }

    /**
     * A user can access a distributor if:
     * - They are the main distributor user (distributor.userId == user.id), or
     * - They are a branch user belonging to this distributor (user.distributorId == distributor.id)
     */
    private boolean canAccessDistributor(User user, Distributor distributor) {
        if (distributor.getUserId().equals(user.getId())) {
            return true;
        }
        return user.getDistributorId() != null
                && user.getDistributorId().getValue().equals(distributor.getId().getValue());
    }
}
