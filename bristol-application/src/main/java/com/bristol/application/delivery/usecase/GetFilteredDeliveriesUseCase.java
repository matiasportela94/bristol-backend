package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.application.delivery.dto.DeliveryPageDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryStatus;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetFilteredDeliveriesUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDtoAssembler deliveryDtoAssembler;

    @Transactional(readOnly = true)
    public DeliveryPageDto execute(
            LocalDate scheduledDate,
            LocalDate dateFrom,
            LocalDate dateTo,
            String orderId,
            int page,
            int size
    ) {
        validateFilters(scheduledDate, dateFrom, dateTo, page, size);

        List<Delivery> filteredDeliveries = selectDeliveries(scheduledDate, dateFrom, dateTo).stream()
                .filter(delivery -> orderId == null || delivery.getOrderId().getValue().toString().equals(orderId))
                .collect(Collectors.toList());

        long totalItems = filteredDeliveries.size();
        int fromIndex = Math.min(page * size, filteredDeliveries.size());
        int toIndex = Math.min(fromIndex + size, filteredDeliveries.size());
        List<DeliveryDto> pagedItems = deliveryDtoAssembler.toDtos(filteredDeliveries.subList(fromIndex, toIndex));

        return DeliveryPageDto.builder()
                .items(pagedItems)
                .page(page)
                .size(size)
                .totalItems(totalItems)
                .totalPages(totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / size))
                .scheduledCount(countByStatus(filteredDeliveries, DeliveryStatus.SCHEDULED, DeliveryStatus.IN_TRANSIT))
                .deliveredCount(countByStatus(filteredDeliveries, DeliveryStatus.DELIVERED))
                .cancelledCount(countByStatus(filteredDeliveries, DeliveryStatus.FAILED))
                .build();
    }

    private List<Delivery> selectDeliveries(LocalDate scheduledDate, LocalDate dateFrom, LocalDate dateTo) {
        if (scheduledDate != null) {
            return deliveryRepository.findByScheduledDate(scheduledDate);
        }

        if (dateFrom != null) {
            return deliveryRepository.findByDateRange(dateFrom, dateTo);
        }

        return deliveryRepository.findAll();
    }

    private long countByStatus(List<Delivery> deliveries, DeliveryStatus... statuses) {
        List<DeliveryStatus> acceptedStatuses = List.of(statuses);
        return deliveries.stream()
                .filter(delivery -> acceptedStatuses.contains(delivery.getStatus()))
                .count();
    }

    private void validateFilters(LocalDate scheduledDate, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        if (scheduledDate != null && (dateFrom != null || dateTo != null)) {
            throw new ValidationException("Use either scheduledDate or dateFrom/dateTo filters, not both");
        }

        if ((dateFrom == null) != (dateTo == null)) {
            throw new ValidationException("dateFrom and dateTo must be provided together");
        }

        if (dateFrom != null && dateFrom.isAfter(dateTo)) {
            throw new ValidationException("dateFrom cannot be after dateTo");
        }

        if (page < 0) {
            throw new ValidationException("page must be zero or positive");
        }

        if (size <= 0) {
            throw new ValidationException("size must be greater than zero");
        }
    }
}
