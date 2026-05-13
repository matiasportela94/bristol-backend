package com.bristol.application.delivery.usecase;

import com.bristol.application.delivery.dto.DeliveryRouteDto;
import com.bristol.application.delivery.dto.DeliveryRouteStopDto;
import com.bristol.application.delivery.route.RouteOptimizer;
import com.bristol.application.delivery.route.RouteOptimizerResult;
import com.bristol.domain.delivery.Delivery;
import com.bristol.domain.delivery.DeliveryRepository;
import com.bristol.domain.delivery.DeliveryStatus;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateDeliveryRouteUseCase {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RouteOptimizer routeOptimizer;

    @Value("${bristol.depot.address:Av. Colón 1234, Mar del Plata}")
    private String depotAddress;

    @Value("${bristol.depot.departure-time:08:00}")
    private String departureTime;

    @Transactional(readOnly = true)
    public DeliveryRouteDto execute(LocalDate date) {
        List<Delivery> deliveries = deliveryRepository.findByScheduledDate(date).stream()
                .filter(d -> d.getStatus() == DeliveryStatus.SCHEDULED
                          || d.getStatus() == DeliveryStatus.IN_TRANSIT)
                .toList();

        if (deliveries.isEmpty()) {
            throw new ValidationException("No hay entregas activas para la fecha: " + date);
        }

        // Build parallel lists: delivery → order → address string
        List<Order> orders = new ArrayList<>();
        List<String> addresses = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            Order order = orderRepository.findById(delivery.getOrderId())
                    .orElseThrow(() -> new ValidationException(
                            "Orden no encontrada para la entrega " + delivery.getId().getValue()));
            orders.add(order);
            addresses.add(order.getShippingAddress().getFullAddress() + ", Mar del Plata, Argentina");
        }

        RouteOptimizerResult result = routeOptimizer.optimize(
                depotAddress + ", Mar del Plata, Argentina", addresses);

        return buildRouteDto(date, deliveries, orders, addresses, result);
    }

    private DeliveryRouteDto buildRouteDto(
            LocalDate date,
            List<Delivery> deliveries,
            List<Order> orders,
            List<String> addresses,
            RouteOptimizerResult result
    ) {
        List<Integer> order = result.getWaypointOrder();
        List<Integer> durations = result.getLegDurationsSeconds();
        List<Integer> distances = result.getLegDistancesMeters();

        LocalTime cursor = LocalTime.parse(departureTime, TIME_FMT);
        // First leg: depot → first stop
        if (!durations.isEmpty()) {
            cursor = cursor.plusSeconds(durations.get(0));
        }

        List<DeliveryRouteStopDto> stops = new ArrayList<>();
        int totalDistance = distances.stream().mapToInt(Integer::intValue).sum();
        int totalDuration = durations.stream().mapToInt(Integer::intValue).sum();

        for (int i = 0; i < order.size(); i++) {
            int originalIdx = order.get(i);
            Delivery delivery = deliveries.get(originalIdx);
            Order o = orders.get(originalIdx);

            User user = userRepository.findById(o.getUserId()).orElse(null);
            String customerName = user != null
                    ? user.getFirstName() + " " + user.getLastName()
                    : "Cliente desconocido";

            stops.add(DeliveryRouteStopDto.builder()
                    .stopNumber(i + 1)
                    .deliveryId(delivery.getId().getValue().toString())
                    .deliveryNumber(delivery.getDeliveryNumber())
                    .orderId(o.getId().getValue().toString())
                    .orderNumber(o.getOrderNumber())
                    .customerName(customerName)
                    .address(o.getShippingAddress().getFullAddress())
                    .estimatedArrival(cursor.format(TIME_FMT))
                    .legDistanceMeters(i < distances.size() ? distances.get(i) : 0)
                    .legDurationSeconds(i < durations.size() ? durations.get(i) : 0)
                    .build());

            // Advance cursor to next leg (stop i+1)
            int nextLegIdx = i + 1;
            if (nextLegIdx < durations.size()) {
                cursor = cursor.plusSeconds(durations.get(nextLegIdx));
            }
        }

        String mapsUrl = buildGoogleMapsUrl(addresses, order);

        return DeliveryRouteDto.builder()
                .date(date.toString())
                .depot(depotAddress)
                .totalStops(stops.size())
                .totalDistanceMeters(totalDistance)
                .totalDurationSeconds(totalDuration)
                .stops(stops)
                .googleMapsUrl(mapsUrl)
                .build();
    }

    private String buildGoogleMapsUrl(List<String> addresses, List<Integer> order) {
        try {
            String depot = depotAddress + ", Mar del Plata, Argentina";
            String waypointsParam = order.stream()
                    .map(addresses::get)
                    .collect(java.util.stream.Collectors.joining("|"));

            return "https://www.google.com/maps/dir/?api=1"
                    + "&origin=" + encode(depot)
                    + "&destination=" + encode(depot)
                    + "&waypoints=" + encode(waypointsParam)
                    + "&travelmode=driving";
        } catch (Exception e) {
            return "";
        }
    }

    private String encode(String value) throws java.io.UnsupportedEncodingException {
        return java.net.URLEncoder.encode(value, "UTF-8");
    }
}
