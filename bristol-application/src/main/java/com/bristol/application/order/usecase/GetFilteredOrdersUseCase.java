package com.bristol.application.order.usecase;

import com.bristol.application.order.dto.OrderDto;
import com.bristol.application.order.dto.OrderFilterRequest;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
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

    @Transactional(readOnly = true)
    public List<OrderDto> execute(OrderFilterRequest filter) {
        List<Order> orders;

        // If no filters, return all orders
        if (isNoFiltersApplied(filter)) {
            orders = orderRepository.findAll();
        }
        // Filter by user and status
        else if (filter.getUserId() != null && filter.getStatus() != null) {
            UserId userId = new UserId(UUID.fromString(filter.getUserId()));
            orders = orderRepository.findByUserIdAndStatus(userId, filter.getStatus());
        }
        // Filter by user only
        else if (filter.getUserId() != null) {
            UserId userId = new UserId(UUID.fromString(filter.getUserId()));
            orders = orderRepository.findByUserId(userId);
        }
        // Filter by status only
        else if (filter.getStatus() != null) {
            orders = orderRepository.findByStatus(filter.getStatus());
        }
        // Filter by distributor only
        else if (filter.getDistributorId() != null) {
            DistributorId distributorId = new DistributorId(UUID.fromString(filter.getDistributorId()));
            orders = orderRepository.findByDistributorId(distributorId);
        }
        // Filter by date range
        else if (filter.getStartDate() != null && filter.getEndDate() != null) {
            orders = orderRepository.findByDateRange(filter.getStartDate(), filter.getEndDate());
        }
        // Default: all orders
        else {
            orders = orderRepository.findAll();
        }

        // Apply additional in-memory filters if needed
        if (filter.getStartDate() != null || filter.getEndDate() != null) {
            orders = applyDateFilter(orders, filter.getStartDate(), filter.getEndDate());
        }

        if (filter.getDistributorId() != null && filter.getUserId() == null && filter.getStatus() == null) {
            // Already filtered by distributor above
        } else if (filter.getDistributorId() != null) {
            DistributorId distributorId = new DistributorId(UUID.fromString(filter.getDistributorId()));
            orders = applyDistributorFilter(orders, distributorId);
        }

        Map<String, String> distributorNames = distributorRepository.findAll().stream()
                .collect(Collectors.toMap(
                        distributor -> distributor.getId().getValue().toString(),
                        distributor -> distributor.getRazonSocial()
                ));
        Map<String, CustomerSummary> customersById = userRepository.findAll().stream()
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

    private boolean isNoFiltersApplied(OrderFilterRequest filter) {
        return filter.getStatus() == null
                && filter.getDistributorId() == null
                && filter.getStartDate() == null
                && filter.getEndDate() == null
                && filter.getUserId() == null;
    }

    private List<Order> applyDateFilter(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        return orders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getOrderDate()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                    boolean afterStart = startDate == null || !orderDate.isBefore(startDate);
                    boolean beforeEnd = endDate == null || !orderDate.isAfter(endDate);
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());
    }

    private List<Order> applyDistributorFilter(List<Order> orders, DistributorId distributorId) {
        return orders.stream()
                .filter(order -> distributorId.equals(order.getDistributorId()))
                .collect(Collectors.toList());
    }
}
