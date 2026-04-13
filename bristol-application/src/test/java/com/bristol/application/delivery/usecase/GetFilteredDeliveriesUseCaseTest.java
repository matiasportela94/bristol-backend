package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryDto;
import com.bristol.application.delivery.dto.DeliveryPageDto;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryCalendarId;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.order.OrderId;
import com.bristol.domain.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetFilteredDeliveriesUseCaseTest {

    @Test
    void executeShouldReturnDeliveriesForExactDate() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        LocalDate scheduledDate = LocalDate.parse("2026-04-14");
        List<Delivery> deliveries = List.of(sampleDelivery(scheduledDate));
        List<DeliveryDto> expectedItems = List.of(new DeliveryDto());

        when(deliveryRepository.findByScheduledDate(scheduledDate)).thenReturn(deliveries);
        when(deliveryDtoAssembler.toDtos(deliveries)).thenReturn(expectedItems);

        DeliveryPageDto result = useCase.execute(scheduledDate, null, null, null, 0, 10);

        assertEquals(expectedItems, result.getItems());
        assertEquals(1, result.getTotalItems());
        verify(deliveryRepository).findByScheduledDate(scheduledDate);
    }

    @Test
    void executeShouldReturnDeliveriesForDateRange() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        LocalDate dateFrom = LocalDate.parse("2026-04-14");
        LocalDate dateTo = LocalDate.parse("2026-04-16");
        List<Delivery> deliveries = List.of(sampleDelivery(dateFrom), sampleDelivery(dateTo));
        List<DeliveryDto> expectedItems = List.of(new DeliveryDto(), new DeliveryDto());

        when(deliveryRepository.findByDateRange(dateFrom, dateTo)).thenReturn(deliveries);
        when(deliveryDtoAssembler.toDtos(deliveries)).thenReturn(expectedItems);

        DeliveryPageDto result = useCase.execute(null, dateFrom, dateTo, null, 0, 10);

        assertEquals(expectedItems, result.getItems());
        assertEquals(2, result.getTotalItems());
        verify(deliveryRepository).findByDateRange(dateFrom, dateTo);
    }

    @Test
    void executeShouldRejectMixedExactDateAndRangeFilters() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        assertThrows(
                ValidationException.class,
                () -> useCase.execute(LocalDate.parse("2026-04-14"), LocalDate.parse("2026-04-14"), LocalDate.parse("2026-04-15"), null, 0, 10)
        );
    }

    @Test
    void executeShouldRejectIncompleteRangeFilters() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        assertThrows(
                ValidationException.class,
                () -> useCase.execute(null, LocalDate.parse("2026-04-14"), null, null, 0, 10)
        );
    }

    @Test
    void executeShouldRejectInvalidRangeOrder() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        assertThrows(
                ValidationException.class,
                () -> useCase.execute(null, LocalDate.parse("2026-04-16"), LocalDate.parse("2026-04-14"), null, 0, 10)
        );
    }

    @Test
    void executeShouldPaginateResults() {
        DeliveryRepository deliveryRepository = mock(DeliveryRepository.class);
        DeliveryDtoAssembler deliveryDtoAssembler = mock(DeliveryDtoAssembler.class);
        GetFilteredDeliveriesUseCase useCase = new GetFilteredDeliveriesUseCase(deliveryRepository, deliveryDtoAssembler);

        List<Delivery> deliveries = List.of(
                sampleDelivery(LocalDate.parse("2026-04-14")),
                sampleDelivery(LocalDate.parse("2026-04-15")),
                sampleDelivery(LocalDate.parse("2026-04-16"))
        );
        List<DeliveryDto> secondPageItems = List.of(new DeliveryDto());

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        when(deliveryDtoAssembler.toDtos(deliveries.subList(2, 3))).thenReturn(secondPageItems);

        DeliveryPageDto result = useCase.execute(null, null, null, null, 1, 2);

        assertEquals(secondPageItems, result.getItems());
        assertEquals(3, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
    }

    private static Delivery sampleDelivery(LocalDate scheduledDate) {
        return Delivery.schedule(
                OrderId.generate(),
                DeliveryCalendarId.generate(),
                scheduledDate,
                null,
                Instant.parse("2026-04-13T12:00:00Z")
        );
    }
}
