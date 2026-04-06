package com.bristol.infrastructure.loader;

import com.bristol.domain.delivery.DeliveryZoneType;
import com.bristol.domain.distributor.Distributor;
import com.bristol.domain.distributor.DistributorId;
import com.bristol.domain.distributor.DistributorRepository;
import com.bristol.domain.product.BeerType;
import com.bristol.domain.product.Product;
import com.bristol.domain.product.ProductCategory;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.product.ProductRepository;
import com.bristol.domain.product.ProductSubcategory;
import com.bristol.domain.product.ProductVariant;
import com.bristol.domain.product.ProductVariantId;
import com.bristol.domain.product.ProductVariantRepository;
import com.bristol.domain.shared.valueobject.Money;
import com.bristol.domain.user.User;
import com.bristol.domain.user.UserId;
import com.bristol.domain.user.UserRepository;
import com.bristol.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Loads initial data into the database on application startup.
 * Creates baseline users and optional demo data for local development.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private static final UUID DEMO_USER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final UUID DEMO_DISTRIBUTOR_USER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID DEMO_PRODUCT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID DEMO_VARIANT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID DEMO_DISTRIBUTOR_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

    private final UserRepository userRepository;
    private final DistributorRepository distributorRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bristol.bootstrap.demo-data:false}")
    private boolean bootstrapDemoData;

    @Value("${bristol.bootstrap.admin.email:admin@bristol.com}")
    private String adminEmail;

    @Value("${bristol.bootstrap.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${bristol.bootstrap.admin.first-name:Super}")
    private String adminFirstName;

    @Value("${bristol.bootstrap.admin.last-name:Admin}")
    private String adminLastName;

    @Value("${bristol.bootstrap.demo-user.email:demo.user@bristol.com}")
    private String demoUserEmail;

    @Value("${bristol.bootstrap.demo-user.password:User123!}")
    private String demoUserPassword;

    @Value("${bristol.bootstrap.demo-distributor.email:demo.distributor@bristol.com}")
    private String demoDistributorEmail;

    @Value("${bristol.bootstrap.demo-distributor.password:Distributor123!}")
    private String demoDistributorPassword;

    @Override
    public void run(String... args) {
        createSuperAdminIfNotExists();
//        if (bootstrapDemoData) {
//            createDemoDataIfEnabled();
//        }
    }

    private void createSuperAdminIfNotExists() {
        var existingAdmin = userRepository.findByEmail(adminEmail);
        if (existingAdmin.isPresent()) {
            User user = existingAdmin.get();
            if (user.getRole() == UserRole.ADMIN) {
                log.info("Super admin user already exists: {}", adminEmail);
                return;
            }

            User promotedAdmin = user.toBuilder()
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(promotedAdmin);
            log.warn("Promoted existing user to bootstrap admin role: {}", adminEmail);
            return;
        }

        String hashedPassword = passwordEncoder.encode(adminPassword);

        User adminUser = User.create(
                adminEmail,
                hashedPassword,
                adminFirstName,
                adminLastName,
                UserRole.ADMIN,
                Instant.now()
        );

        userRepository.save(adminUser);

        log.info("Created bootstrap admin user: {}", adminEmail);
        log.warn("IMPORTANT: Change the admin password in production!");
    }

    private void createDemoDataIfEnabled() {
        User demoUser = createDemoUserIfNotExists();
        createDemoProductIfNotExists();
        createDemoDistributorIfNotExists();
        log.info("Development demo data ensured for user {}", demoUser.getEmail());
    }

    private User createDemoUserIfNotExists() {
        return userRepository.findByEmail(demoUserEmail)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    User demoUser = User.create(
                            demoUserEmail,
                            passwordEncoder.encode(demoUserPassword),
                            "Demo",
                            "User",
                            UserRole.USER,
                            now
                    ).toBuilder()
                            .id(new UserId(DEMO_USER_ID))
                            .build();

                    User savedUser = userRepository.save(demoUser);
                    log.info("Created demo user: {}", savedUser.getEmail());
                    return savedUser;
                });
    }

    private void createDemoProductIfNotExists() {
        boolean productExists = productRepository.findById(new ProductId(DEMO_PRODUCT_ID)).isPresent();
        if (!productExists) {
            Instant now = Instant.now();
            Product demoProduct = Product.create(
                    "Demo IPA 473",
                    "Producto demo para desarrollo y smoke tests",
                    ProductCategory.PRODUCTOS,
                    ProductSubcategory.SIX_PACK,
                    BeerType.IPA,
                    Money.of(3500),
                    48,
                    5,
                    now
            ).toBuilder()
                    .id(new ProductId(DEMO_PRODUCT_ID))
                    .featured(true)
                    .build();

            productRepository.save(demoProduct);
            log.info("Created demo product: {}", demoProduct.getName());
        }

        boolean variantExists = productVariantRepository.findById(new ProductVariantId(DEMO_VARIANT_ID)).isPresent();
        if (!variantExists) {
            Instant now = Instant.now();
            ProductVariant demoVariant = ProductVariant.create(
                    new ProductId(DEMO_PRODUCT_ID),
                    "DEMO-IPA-473",
                    "473ml",
                    "Amber",
                    Money.zero(),
                    24,
                    "https://example.com/demo-ipa-473.png",
                    now
            ).toBuilder()
                    .id(new ProductVariantId(DEMO_VARIANT_ID))
                    .build();

            productVariantRepository.save(demoVariant);
            log.info("Created demo product variant: {}", demoVariant.getSku());
        }
    }

    private void createDemoDistributorIfNotExists() {
        User distributorUser = userRepository.findByEmail(demoDistributorEmail)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    User newDistributorUser = User.create(
                            demoDistributorEmail,
                            passwordEncoder.encode(demoDistributorPassword),
                            "Demo",
                            "Distributor",
                            UserRole.USER,
                            now
                    ).toBuilder()
                            .id(new UserId(DEMO_DISTRIBUTOR_USER_ID))
                            .isDistributor(true)
                            .build();

                    User savedUser = userRepository.save(newDistributorUser);
                    log.info("Created demo distributor user: {}", savedUser.getEmail());
                    return savedUser;
                });

        if (distributorRepository.findById(new DistributorId(DEMO_DISTRIBUTOR_ID)).isPresent()) {
            return;
        }

        Distributor demoDistributor = Distributor.create(
                distributorUser.getId(),
                "Deposito Demo 123",
                "1122334455",
                "30111222",
                "20-30111222-3",
                "Distribuidora Demo SRL",
                null,
                DeliveryZoneType.SUR.getDeliveryZoneId(),
                Instant.now()
        ).approve(Instant.now()).toBuilder()
                .id(new DistributorId(DEMO_DISTRIBUTOR_ID))
                .build();

        distributorRepository.save(demoDistributor);
        log.info("Created demo distributor: {}", demoDistributor.getRazonSocial());
    }
}
