package com.bristol.application.distributor.usecase;

import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Updates denormalized distributor order stats when an order is paid or cancelled.
 */
@Service
@RequiredArgsConstructor
public class DistributorOrderStatsService {

    private final DistributorRepository distributorRepository;

    public void recordPaidOrder(Order order, Instant now) {
        findDistributorForOrder(order).ifPresent(distributor -> {
            Distributor updated = distributor.recordOrder(
                    order.getTotal().getAmount(),
                    order.getTotalBeerCount(),
                    now
            );
            distributorRepository.save(updated);
        });
    }

    private Optional<Distributor> findDistributorForOrder(Order order) {
        UserId userId = order.getUserId();
        if (userId == null) {
            return Optional.empty();
        }
        return distributorRepository.findByUserId(userId);
    }
}
