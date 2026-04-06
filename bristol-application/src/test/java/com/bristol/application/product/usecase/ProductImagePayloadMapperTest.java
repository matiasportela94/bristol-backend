package com.bristol.application.product.usecase;

import com.bristol.application.product.dto.ProductImageRequest;
import com.bristol.domain.product.ProductId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImagePayloadMapperTest {

    private static final String PIXEL_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO7Zx1EAAAAASUVORK5CYII=";

    private final ProductImagePayloadMapper mapper = new ProductImagePayloadMapper();

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
}
