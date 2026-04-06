package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldAllowSpecialProductsWithoutPrice() {
        assertThatCode(() -> Product.create(
                "Ploteado de ventana",
                "Precio a convenir",
                ProductCategory.ESPECIALES,
                ProductSubcategory.PLOTEO,
                null,
                null,
                0,
                0,
                Instant.parse("2026-04-02T15:00:00Z")
        )).doesNotThrowAnyException();
    }

    @Test
    void shouldRequirePriceForNonSpecialProducts() {
        assertThatThrownBy(() -> Product.create(
                "Six Pack IPA",
                "Beer product",
                ProductCategory.PRODUCTOS,
                ProductSubcategory.SIX_PACK,
                BeerType.IPA,
                null,
                10,
                2,
                Instant.parse("2026-04-02T15:00:00Z")
        )).isInstanceOf(ValidationException.class)
                .hasMessage("Product price must be positive");
    }
}
