package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.domain.product.ProductId;
import com.bristol.domain.shared.time.TimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImagePayloadMapperTest {

    private static final String PIXEL_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO7Zx1EAAAAASUVORK5CYII=";

    private final ProductImagePayloadMapper mapper = new ProductImagePayloadMapper(fixedTimeProvider());

    @Test
    void toDomainListShouldAssignPrimaryImageWhenNoneIsProvided() {
        ProductId productId = ProductId.generate();

        List<ProductImageRequest> requests = List.of(
                ProductImageRequest.builder()
                        .fileName("front.png")
                        .contentType("image/png")
                        .dataBase64(PIXEL_BASE64)
                        .displayOrder(0)
                        .build(),
                ProductImageRequest.builder()
                        .fileName("back.png")
                        .contentType("image/png")
                        .dataBase64("data:image/png;base64," + PIXEL_BASE64)
                        .displayOrder(1)
                        .build()
        );

        var images = mapper.toDomainList(productId, requests);

        assertThat(images).hasSize(2);
        assertThat(images.get(0).isPrimary()).isTrue();
        assertThat(images.get(1).isPrimary()).isFalse();
        assertThat(images.get(1).getImageData()).isNotEmpty();
    }

    @Test
    void toPrimaryImageDataUrlShouldBuildDataUrl() {
        ProductId productId = ProductId.generate();
        var images = mapper.toDomainList(productId, List.of(
                ProductImageRequest.builder()
                        .fileName("front.png")
                        .contentType("image/png")
                        .dataBase64(PIXEL_BASE64)
                        .primary(true)
                        .build()
        ));

        String dataUrl = mapper.toPrimaryImageDataUrl(images);

        assertThat(dataUrl).startsWith("data:image/png;base64,");
        assertThat(dataUrl).contains(PIXEL_BASE64);
    }

    private static TimeProvider fixedTimeProvider() {
        Instant fixedInstant = Instant.parse("2026-04-14T12:00:00Z");
        return new TimeProvider() {
            @Override
            public Instant now() {
                return fixedInstant;
            }

            @Override
            public LocalDateTime nowDateTime() {
                return LocalDateTime.of(2026, 4, 14, 9, 0);
            }

            @Override
            public LocalDate nowDate() {
                return LocalDate.of(2026, 4, 14);
            }
        };
    }
}
