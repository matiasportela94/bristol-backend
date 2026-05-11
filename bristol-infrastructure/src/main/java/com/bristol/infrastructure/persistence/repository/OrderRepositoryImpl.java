package com.bristol.infrastructure.persistence.repository;

import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.shared.Page;
import com.bristol.domain.shared.time.TimeProvider;
import com.bristol.domain.user.UserId;
import com.bristol.infrastructure.persistence.entity.OrderEntity;
import com.bristol.infrastructure.persistence.entity.OrderItemEntity;
import com.bristol.infrastructure.persistence.mapper.OrderMapper;
import com.bristol.infrastructure.persistence.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OrderRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaRepository;
    private final JpaOrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final TimeProvider timeProvider;

    @Override
    public Order save(Order order) {
        var entity = orderMapper.toEntity(order);
        if (entity.getOrderNumber() == null) {
            entity.setOrderNumber(jpaRepository.nextOrderNumber());
        }
        var saved = jpaRepository.save(entity);

        orderItemRepository.deleteByOrderId(saved.getId());
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toList());
        orderItemRepository.saveAll(itemEntities);

        return attachItems(orderMapper.toDomain(saved), order.getItems());
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return jpaRepository.findById(orderId.getValue())
                .map(orderMapper::toDomain)
                .map(this::attachItems);
    }

    @Override
    public List<Order> findByUserId(UserId userId) {
        return attachItems(jpaRepository.findByUserId(userId.getValue()).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        var entityStatus = OrderEntity.OrderStatusEnum.valueOf(status.name());
        return attachItems(jpaRepository.findByOrderStatus(entityStatus).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findByDistributorId(DistributorId distributorId) {
        return attachItems(jpaRepository.findByDistributorId(distributorId.getValue()).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status) {
        var entityStatus = OrderEntity.OrderStatusEnum.valueOf(status.name());
        return attachItems(jpaRepository.findByUserIdAndOrderStatus(userId.getValue(), entityStatus).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Instant startInstant = timeProvider.startOfDay(startDate);
        Instant endInstant = timeProvider.endOfDay(endDate);

        return attachItems(jpaRepository.findByDateRange(startInstant, endInstant).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findPendingOrdersOlderThan(LocalDate date) {
        Instant instant = timeProvider.startOfDay(date);
        return attachItems(jpaRepository.findPendingOrdersOlderThan(instant).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean existsById(OrderId orderId) {
        return jpaRepository.existsById(orderId.getValue());
    }

    @Override
    public List<Order> findAll() {
        return attachItems(jpaRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Order> findWithFilters(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            DistributorId distributorId,
            UserId userId
    ) {
        String statusString = status != null ? status.name() : null;
        UUID distributorUuid = distributorId != null ? distributorId.getValue() : null;
        UUID userUuid = userId != null ? userId.getValue() : null;
        Instant startInstant = startDate != null ? timeProvider.startOfDay(startDate) : null;
        Instant endInstant = endDate != null ? timeProvider.endOfDay(endDate) : null;

        return attachItems(jpaRepository.findWithFilters(
                statusString,
                startInstant,
                endInstant,
                distributorUuid,
                userUuid
        ).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public Page<Order> findWithFiltersPaginated(
            OrderId orderId,
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            DistributorId distributorId,
            UserId userId,
            int pageNumber,
            int pageSize
    ) {
        String orderIdString = orderId != null ? orderId.getValue().toString() : null;
        String statusString = status != null ? status.name() : null;
        String distributorIdString = distributorId != null ? distributorId.getValue().toString() : null;
        String userIdString = userId != null ? userId.getValue().toString() : null;
        Instant startInstant = startDate != null ? timeProvider.startOfDay(startDate) : null;
        Instant endInstant = endDate != null ? timeProvider.endOfDay(endDate) : null;

        // Create pageable without sort (query already has ORDER BY)
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        org.springframework.data.domain.Page<OrderEntity> entityPage = jpaRepository.findWithFiltersPaginated(
                orderIdString,
                statusString,
                startInstant,
                endInstant,
                distributorIdString,
                userIdString,
                pageRequest
        );

        // Convert entities to domain and attach items
        List<Order> orders = attachItems(entityPage.getContent().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList()));

        return new Page<>(
                orders,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements()
        );
    }

    @Override
    public long countByStatus(OrderStatus status) {
        OrderEntity.OrderStatusEnum entityStatus = status != null ?
                OrderEntity.OrderStatusEnum.valueOf(status.name()) : null;
        return jpaRepository.countByOrderStatus(entityStatus);
    }

    @Override
    public void delete(OrderId orderId) {
        orderItemRepository.deleteByOrderId(orderId.getValue());
        jpaRepository.deleteById(orderId.getValue());
    }

    private Order attachItems(Order order) {
        List<com.bristol.domain.order.OrderItem> items = orderItemRepository.findByOrderId(order.getId().getValue()).stream()
                .map(orderItemMapper::toDomain)
                .collect(Collectors.toList());
        return attachItems(order, items);
    }

    private List<Order> attachItems(List<Order> orders) {
        if (orders.isEmpty()) {
            return orders;
        }

        List<UUID> orderIds = orders.stream()
                .map(order -> order.getId().getValue())
                .collect(Collectors.toList());

        Map<UUID, List<com.bristol.domain.order.OrderItem>> itemsByOrderId = orderItemRepository.findByOrderIdIn(orderIds).stream()
                .map(orderItemMapper::toDomain)
                .collect(Collectors.groupingBy(item -> item.getOrderId().getValue()));

        return orders.stream()
                .map(order -> attachItems(order, itemsByOrderId.getOrDefault(order.getId().getValue(), List.of())))
                .collect(Collectors.toList());
    }

    private Order attachItems(Order order, Collection<com.bristol.domain.order.OrderItem> items) {
        return order.toBuilder()
                .items(items.stream().collect(Collectors.toList()))
                .build();
    }
}
