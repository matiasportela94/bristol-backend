package com.bristol.domain.product;

import com.bristol.domain.shared.exception.ValidationException;
import com.bristol.domain.shared.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void shouldAllowSpecialProductsWithRequiresQuote() {
        assertThatCode(() -> SpecialProduct.create(
                "Ploteado de ventana",
                "Precio a convenir",
                null,
                null,
                null,
                true,
                0,
                0,
                Instant.parse("2026-04-02T15:00:00Z")
        )).doesNotThrowAnyException();
    }

    @Test
    void shouldRequirePriceForBeerProducts() {
        assertThatThrownBy(() -> BeerProduct.create(
                "Six Pack IPA",
                "Beer product",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                10,
                2,
                Instant.parse("2026-04-02T15:00:00Z")
        )).isInstanceOf(ValidationException.class);
    }
}
