package com.bristol.application.stats.usecase;

import com.bristol.application.stats.dto.StockStatDto;
import com.bristol.domain.distributor.DistributorRegistrationRepository;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.order.OrderRepository;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsQueryServiceTest {

    @Test
    void getProductStockStatsShouldConvertPackStockToCanEquivalents() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        DistributorRepository distributorRepository = mock(DistributorRepository.class);
        DistributorRegistrationRepository distributorRegistrationRepository = mock(DistributorRegistrationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);

        StatisticsQueryService service = new StatisticsQueryService(
                orderRepository,
                distributorRepository,
                distributorRegistrationRepository,
                userRepository,
                productRepository,
                productVariantRepository
        );

        Product canProduct = sampleProduct("Pale Ale lata", ProductSubcategory.CAN, BeerType.PALE_ALE, 5);
        Product sixPackProduct = sampleProduct("Pale Ale six pack", ProductSubcategory.SIX_PACK, BeerType.PALE_ALE, 2);
        Product twentyFourPackProduct = sampleProduct("IPA 24 pack", ProductSubcategory.TWENTY_FOUR_PACK, BeerType.IPA, 1);

        when(productRepository.findAll()).thenReturn(List.of(canProduct, sixPackProduct, twentyFourPackProduct));
        when(productVariantRepository.findByProductId(canProduct.getId())).thenReturn(List.of());
        when(productVariantRepository.findByProductId(sixPackProduct.getId())).thenReturn(List.of());
        when(productVariantRepository.findByProductId(twentyFourPackProduct.getId())).thenReturn(List.of());

        List<StockStatDto> result = service.getProductStockStats();

        assertThat(result)
                .extracting(StockStatDto::getLabel, StockStatDto::getValue)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("Pale Ale", 17),
                        org.assertj.core.groups.Tuple.tuple("IPA", 24)
                );
    }

    @Test
    void getProductStockStatsShouldUseVariantStockWhenProductHasVariants() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        DistributorRepository distributorRepository = mock(DistributorRepository.class);
        DistributorRegistrationRepository distributorRegistrationRepository = mock(DistributorRegistrationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);

        StatisticsQueryService service = new StatisticsQueryService(
                orderRepository,
                distributorRepository,
                distributorRegistrationRepository,
                userRepository,
                productRepository,
                productVariantRepository
        );

        Product sixPackProduct = sampleProduct("Lager six pack", ProductSubcategory.SIX_PACK, BeerType.LAGER, 99);
        ProductVariant firstVariant = ProductVariant.create(
                sixPackProduct.getId(),
                "LAGER-6PK-A",
                "473ml",
                null,
                Money.zero(),
                3,
                null,
                Instant.parse("2026-04-14T12:00:00Z")
        );
        ProductVariant secondVariant = ProductVariant.create(
                sixPackProduct.getId(),
                "LAGER-6PK-B",
                "354ml",
                null,
                Money.zero(),
                2,
                null,
                Instant.parse("2026-04-14T12:00:00Z")
        );

        when(productRepository.findAll()).thenReturn(List.of(sixPackProduct));
        when(productVariantRepository.findByProductId(sixPackProduct.getId())).thenReturn(List.of(firstVariant, secondVariant));

        List<StockStatDto> result = service.getProductStockStats();

        assertThat(result).singleElement().satisfies(stat -> {
            assertThat(stat.getLabel()).isEqualTo("Lager");
            assertThat(stat.getValue()).isEqualTo(30);
        });
    }

    @Test
    void getMerchStockStatsShouldGroupMerchBySubcategory() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        DistributorRepository distributorRepository = mock(DistributorRepository.class);
        DistributorRegistrationRepository distributorRegistrationRepository = mock(DistributorRegistrationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductVariantRepository productVariantRepository = mock(ProductVariantRepository.class);

        StatisticsQueryService service = new StatisticsQueryService(
                orderRepository,
                distributorRepository,
                distributorRegistrationRepository,
                userRepository,
                productRepository,
                productVariantRepository
        );

        Product remera = sampleMerchProduct("Remera Bristol", ProductSubcategory.REMERA, 8);
        Product gorra = sampleMerchProduct("Gorra Bristol", ProductSubcategory.GORRA, 3);
        Product beer = sampleProduct("IPA lata", ProductSubcategory.CAN, BeerType.IPA, 10);

        when(productRepository.findAll()).thenReturn(List.of(remera, gorra, beer));
        when(productVariantRepository.findByProductId(remera.getId())).thenReturn(List.of());
        when(productVariantRepository.findByProductId(gorra.getId())).thenReturn(List.of());

        List<StockStatDto> result = service.getMerchStockStats();

        assertThat(result)
                .extracting(StockStatDto::getLabel, StockStatDto::getValue)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("Remeras", 8),
                        org.assertj.core.groups.Tuple.tuple("Gorras", 3)
                );
    }

    private static Product sampleProduct(String name, ProductSubcategory subcategory, BeerType beerType, int stockQuantity) {
        return Product.create(
                name,
                "demo",
                ProductCategory.PRODUCTOS,
                subcategory,
                beerType,
                Money.of(1000),
                stockQuantity,
                1,
                Instant.parse("2026-04-14T12:00:00Z")
        );
    }

    private static Product sampleMerchProduct(String name, ProductSubcategory subcategory, int stockQuantity) {
        return Product.create(
                name,
                "demo",
                ProductCategory.MERCHANDISING,
                subcategory,
                null,
                Money.of(1000),
                stockQuantity,
                1,
                Instant.parse("2026-04-14T12:00:00Z")
        );
    }
}
