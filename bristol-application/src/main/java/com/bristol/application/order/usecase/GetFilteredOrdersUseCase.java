package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderFilterRequest;
import com.bristol.domain.distributor.DistributorBranchId;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case to get orders with filters.
 */
@Service
@RequiredArgsConstructor
public class GetFilteredOrdersUseCase {

    private final OrderRepository orderRepository;
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final TimeProvider timeProvider;

    @Transactional(readOnly = true)
    public List<OrderDto> execute(OrderFilterRequest filter) {
        // Parse IDs
        DistributorId distributorId = filter.getDistributorId() != null && !filter.getDistributorId().isBlank()
                ? new DistributorId(UUID.fromString(filter.getDistributorId()))
                : null;

        UserId userId = filter.getUserId() != null && !filter.getUserId().isBlank()
                ? new UserId(UUID.fromString(filter.getUserId()))
                : null;

        DistributorBranchId branchId = filter.getBranchId() != null && !filter.getBranchId().isBlank()
                ? new DistributorBranchId(UUID.fromString(filter.getBranchId()))
                : null;

        // Use optimized database-level filtering
        List<Order> orders = orderRepository.findWithFilters(
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                distributorId,
                userId,
                branchId
        );

        // Extract unique IDs from the current result set (avoid loading all users/distributors)
        List<UUID> userIds = orders.stream()
                .map(order -> order.getUserId().getValue())
                .distinct()
                .collect(Collectors.toList());

        List<UUID> distributorIds = orders.stream()
                .filter(order -> order.getDistributorId() != null)
                .map(order -> order.getDistributorId().getValue())
                .distinct()
                .collect(Collectors.toList());

        // Build lookup maps for only the users and distributors in this result set
        Map<String, String> distributorNames = distributorIds.isEmpty() ? Map.of() :
                distributorRepository.findAll().stream()
                        .filter(d -> distributorIds.contains(d.getId().getValue()))
                        .collect(Collectors.toMap(
                                distributor -> distributor.getId().getValue().toString(),
                                distributor -> distributor.getRazonSocial()
                        ));

        Map<String, CustomerSummary> customersById = userRepository.findAll().stream()
                .filter(u -> userIds.contains(u.getId().getValue()))
                .collect(Collectors.toMap(
                        user -> user.getId().getValue().toString(),
                        user -> new CustomerSummary(
                                formatCustomerName(user.getFirstName(), user.getLastName(), user.getEmail()),
                                user.getEmail()
                        )
                ));

        return orders.stream()
                .map(order -> {
                    CustomerSummary customer = customersById.get(order.getUserId().getValue().toString());
                    return orderMapper.toDto(
                            order,
                            customer != null ? customer.name() : null,
                            customer != null ? customer.email() : null,
                            order.getDistributorId() != null
                                    ? distributorNames.get(order.getDistributorId().getValue().toString())
                                    : null
                    );
                })
                .collect(Collectors.toList());
    }

    private String formatCustomerName(String firstName, String lastName, String email) {
        String fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
        return !fullName.isEmpty() ? fullName : email;
    }

    private record CustomerSummary(String name, String email) {
    }
}
