package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.AdminDashboardStatsDto;
import com.bristol.application.stats.dto.DistributorStatsDto;
import com.bristol.application.stats.dto.MonthlyRankingDto;
import com.bristol.application.stats.dto.RankingResponseDto;
import com.bristol.application.stats.dto.StockStatDto;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.distributor.RegistrationStatus;
import com.bristol.domain.order.Order;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.order.OrderStatus;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsQueryService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final Set<OrderStatus> REVENUE_STATUSES = EnumSet.of(
            OrderStatus.PAID,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
    );
    private static final Set<OrderStatus> PENDING_PAYMENT_STATUSES = EnumSet.of(
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.PAYMENT_IN_PROCESS
    );
    private static final Set<OrderStatus> OPEN_ORDER_STATUSES = EnumSet.of(
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.PAYMENT_IN_PROCESS,
            OrderStatus.PAID,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED
    );
    private static final Set<OrderStatus> PENDING_DELIVERY_STATUSES = EnumSet.of(
            OrderStatus.PAID,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED
    );

    private final OrderRepository orderRepository;
    private final DistributorRepository distributorRepository;
    private final DistributorRegistrationRepository distributorRegistrationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public AdminDashboardStatsDto getAdminDashboardStats() {
        List<Order> orders = orderRepository.findAll();

        return AdminDashboardStatsDto.builder()
                .totalOrders(orders.size())
                .totalRevenue(sumOrderTotals(filterByStatuses(orders, REVENUE_STATUSES)))
                .pendingOrders((int) orders.stream()
                        .filter(order -> OPEN_ORDER_STATUSES.contains(order.getStatus()))
                        .count())
                .totalDistributors((int) distributorRepository.findAll().stream()
                        .filter(Distributor::isApproved)
                        .count())
                .pendingDistributors(distributorRegistrationRepository.findByStatus(RegistrationStatus.PENDING).size())
                .updatedAt(latestOrderInstant(orders).orElse(Instant.now()))
                .build();
    }

    public DistributorStatsDto getDistributorStats(String distributorId) {
        Distributor distributor = distributorRepository.findById(new DistributorId(distributorId))
                .orElseThrow(() -> new ValidationException("Distributor not found: " + distributorId));

        List<Order> distributorOrders = orderRepository.findByUserId(distributor.getUserId());
        List<Order> revenueOrders = filterByStatuses(distributorOrders, REVENUE_STATUSES);
        YearMonth currentMonth = YearMonth.now(BUSINESS_ZONE);
        List<Order> currentMonthRevenueOrders = filterByMonth(revenueOrders, currentMonth);
        List<Order> pendingPaymentOrders = filterByStatuses(distributorOrders, PENDING_PAYMENT_STATUSES);
        List<Order> pendingDeliveryOrders = filterByStatuses(distributorOrders, PENDING_DELIVERY_STATUSES);
        Order lastRevenueOrder = revenueOrders.stream()
                .max(Comparator.comparing(Order::getOrderDate))
                .orElse(null);

        return DistributorStatsDto.builder()
                .distributorId(distributor.getId().getValue().toString())
                .email(userRepository.findById(distributor.getUserId()).map(user -> user.getEmail()).orElse(null))
                .monthlySpent(sumOrderTotals(currentMonthRevenueOrders))
                .monthlyOrders(currentMonthRevenueOrders.size())
                .totalSpent(sumOrderTotals(revenueOrders))
                .totalOrders(revenueOrders.size())
                .totalBeersOrdered(revenueOrders.stream().mapToInt(Order::getTotalBeerCount).sum())
                .pendingDeliveries(pendingDeliveryOrders.size())
                .pendingPayments(pendingPaymentOrders.size())
                .lastOrderDate(lastRevenueOrder != null ? lastRevenueOrder.getOrderDate() : null)
                .lastOrderAmount(lastRevenueOrder != null ? lastRevenueOrder.getFinalTotal().getAmount() : null)
                .updatedAt(resolveUpdatedAt(distributor, distributorOrders))
                .build();
    }

    public RankingResponseDto getMonthlyRanking(String month, String distributorId, Integer limit) {
        YearMonth targetMonth = resolveMonth(month);
        YearMonth previousMonth = targetMonth.minusMonths(1);
        Map<String, List<Order>> ordersByUserId = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(order -> order.getUserId().getValue().toString()));

        List<MonthlyRankingDto> ranked = assignPositions(distributorRepository.findAll().stream()
                .filter(Distributor::isApproved)
                .map(distributor -> toMonthlyRanking(distributor, ordersByUserId, targetMonth, previousMonth))
                .filter(this::hasMonthlyActivity)
                .sorted(Comparator
                        .comparing(MonthlyRankingDto::getMonthlySpent, Comparator.reverseOrder())
                        .thenComparing(MonthlyRankingDto::getMonthlyOrders, Comparator.reverseOrder())
                        .thenComparing(MonthlyRankingDto::getMonthlyPending, Comparator.reverseOrder())
                        .thenComparing(MonthlyRankingDto::getDistributorName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()));

        Integer currentUserPosition = ranked.stream()
                .filter(item -> distributorId != null && distributorId.equals(item.getDistributorId()))
                .map(MonthlyRankingDto::getPosition)
                .findFirst()
                .orElse(null);

        List<MonthlyRankingDto> visibleRankings = limit != null && limit > 0
                ? ranked.stream().limit(limit).toList()
                : ranked;

        return RankingResponseDto.builder()
                .month(targetMonth.toString())
                .rankings(visibleRankings)
                .currentUserPosition(currentUserPosition)
                .build();
    }

    public List<StockStatDto> getProductStockStats() {
        Map<BeerType, Integer> stockByBeerType = new HashMap<>();

        for (Product product : productRepository.findAll()) {
            if (!product.isBeer() || product.isDeleted() || product.getBeerType() == null) {
                continue;
            }

            int totalStock = resolveProductStock(product);
            if (totalStock <= 0) {
                continue;
            }

            stockByBeerType.merge(product.getBeerType(), totalStock, Integer::sum);
        }

        return stockByBeerType.entrySet().stream()
                .map(entry -> StockStatDto.builder()
                        .label(formatBeerType(entry.getKey()))
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(StockStatDto::getValue, Comparator.reverseOrder()))
                .toList();
    }

    public List<StockStatDto> getMerchStockStats() {
        Map<ProductSubcategory, Integer> stockBySubcategory = new HashMap<>();

        for (Product product : productRepository.findAll()) {
            if (product.getCategory() != ProductCategory.MERCHANDISING || product.isDeleted() || product.getSubcategory() == null) {
                continue;
            }

            int totalStock = resolveProductStock(product);
            if (totalStock <= 0) {
                continue;
            }

            stockBySubcategory.merge(product.getSubcategory(), totalStock, Integer::sum);
        }

        return stockBySubcategory.entrySet().stream()
                .map(entry -> StockStatDto.builder()
                        .label(formatSubcategory(entry.getKey()))
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(StockStatDto::getValue, Comparator.reverseOrder()))
                .toList();
    }

    private MonthlyRankingDto toMonthlyRanking(
            Distributor distributor,
            Map<String, List<Order>> ordersByUserId,
            YearMonth targetMonth,
            YearMonth previousMonth
    ) {
        List<Order> distributorOrders = ordersByUserId.getOrDefault(distributor.getUserId().getValue().toString(), List.of());
        List<Order> currentRevenueOrders = filterByMonth(filterByStatuses(distributorOrders, REVENUE_STATUSES), targetMonth);
        List<Order> currentPendingOrders = filterByMonth(filterByStatuses(distributorOrders, PENDING_PAYMENT_STATUSES), targetMonth);
        List<Order> previousRevenueOrders = filterByMonth(filterByStatuses(distributorOrders, REVENUE_STATUSES), previousMonth);

        return MonthlyRankingDto.builder()
                .position(0)
                .distributorId(distributor.getId().getValue().toString())
                .distributorName(distributor.getRazonSocial())
                .monthlySpent(sumOrderTotals(currentRevenueOrders))
                .monthlyOrders(currentRevenueOrders.size())
                .monthlyPending(sumOrderTotals(currentPendingOrders))
                .previousMonthSpent(sumOrderTotals(previousRevenueOrders))
                .month(targetMonth.toString())
                .build();
    }

    private List<MonthlyRankingDto> assignPositions(List<MonthlyRankingDto> rankings) {
        for (int index = 0; index < rankings.size(); index++) {
            rankings.get(index).setPosition(index + 1);
        }
        return rankings;
    }

    private boolean hasMonthlyActivity(MonthlyRankingDto ranking) {
        return ranking.getMonthlyOrders() > 0
                || ranking.getMonthlySpent().compareTo(BigDecimal.ZERO) > 0
                || ranking.getMonthlyPending().compareTo(BigDecimal.ZERO) > 0;
    }

    private List<Order> filterByStatuses(List<Order> orders, Set<OrderStatus> statuses) {
        return orders.stream()
                .filter(order -> statuses.contains(order.getStatus()))
                .toList();
    }

    private List<Order> filterByMonth(List<Order> orders, YearMonth targetMonth) {
        return orders.stream()
                .filter(order -> YearMonth.from(order.getOrderDate().atZone(BUSINESS_ZONE)).equals(targetMonth))
                .toList();
    }

    private BigDecimal sumOrderTotals(List<Order> orders) {
        return orders.stream()
                .map(order -> order.getFinalTotal().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<Instant> latestOrderInstant(List<Order> orders) {
        return orders.stream()
                .map(Order::getOrderDate)
                .max(Comparator.naturalOrder());
    }

    private Instant resolveUpdatedAt(Distributor distributor, List<Order> orders) {
        return latestOrderInstant(orders)
                .map(latest -> latest.isAfter(distributor.getUpdatedAt()) ? latest : distributor.getUpdatedAt())
                .orElse(distributor.getUpdatedAt());
    }

    private YearMonth resolveMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now(BUSINESS_ZONE);
        }

        try {
            return YearMonth.parse(month);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Month must use YYYY-MM format");
        }
    }

    private int resolveProductStock(Product product) {
        int unitMultiplier = resolveUnitMultiplier(product.getSubcategory());
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        if (variants.isEmpty()) {
            return (product.getStockQuantity() != null ? product.getStockQuantity() : 0) * unitMultiplier;
        }

        return variants.stream()
                .map(ProductVariant::getStockQuantity)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum() * unitMultiplier;
    }

    private int resolveUnitMultiplier(ProductSubcategory subcategory) {
        if (subcategory == null) {
            return 1;
        }

        return switch (subcategory) {
            case CAN -> 1;
            case SIX_PACK -> 6;
            case TWENTY_FOUR_PACK -> 24;
            default -> 1;
        };
    }

    private String formatBeerType(BeerType beerType) {
        return switch (beerType) {
            case IPA -> "IPA";
            case APA -> "APA";
            case LAGER -> "Lager";
            case STOUT -> "Stout";
            case PORTER -> "Porter";
            case PILSNER -> "Pilsner";
            case SOUR -> "Sour";
            case WHEAT -> "Wheat";
            case BLONDE -> "Blonde";
            case AMBER -> "Amber";
            case GOLDEN -> "Golden";
            case PALE_ALE -> "Pale Ale";
            case OTRO -> "Otro";
        };
    }

    private String formatSubcategory(ProductSubcategory subcategory) {
        return switch (subcategory) {
            case REMERA -> "Remeras";
            case BUZO -> "Buzos";
            case GORRA -> "Gorras";
            case VASO -> "Vasos";
            case PLOTEO -> "Ploteos";
            case EVENTO -> "Eventos";
            case OTRO -> "Otros";
            case CAN -> "Latas";
            case SIX_PACK -> "Six Packs";
            case TWENTY_FOUR_PACK -> "24 Packs";
            case KEG -> "Barriles";
            case GROWLER -> "Growlers";
        };
    }
}
