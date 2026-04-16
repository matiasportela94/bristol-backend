package com.bristol.domain.shared.time;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class BristolTimeTest {

    @Test
    void startOfDayShouldUseArgentinaZone() {
        Instant instant = BristolTime.startOfDay(LocalDate.of(2026, 4, 16));

        assertThat(instant.atZone(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDate())
                .isEqualTo(LocalDate.of(2026, 4, 16));
    }

    @Test
    void parseTimestampShouldSupportFormatWithoutSeconds() {
        var parsed = BristolTime.parseTimestamp("16-04-2026 12:30 ART");

        assertThat(parsed.getZone().getId()).isEqualTo("America/Argentina/Buenos_Aires");
        assertThat(parsed.getYear()).isEqualTo(2026);
        assertThat(parsed.getMonthValue()).isEqualTo(4);
        assertThat(parsed.getDayOfMonth()).isEqualTo(16);
        assertThat(parsed.getHour()).isEqualTo(12);
        assertThat(parsed.getMinute()).isEqualTo(30);
        assertThat(parsed.getSecond()).isEqualTo(0);
    }
}
