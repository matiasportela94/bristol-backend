package com.bristol.domain.shared.valueobject;

import com.bristol.domain.shared.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Rating value object.
 * Represents a rating from 1 to 5.
 */
@Getter
@EqualsAndHashCode
public class Rating {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final int value;

    private Rating(int value) {
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new ValidationException(
                    String.format("Rating must be between %d and %d", MIN_RATING, MAX_RATING)
            );
        }
        this.value = value;
    }

    public static Rating of(int value) {
        return new Rating(value);
    }

    /**
     * Calculate average rating from total and count.
     */
    public static double average(long totalRating, long count) {
        if (count == 0) {
            return 0.0;
        }
        return (double) totalRating / count;
    }

    @Override
    public String toString() {
        return value + "/5";
    }
}
