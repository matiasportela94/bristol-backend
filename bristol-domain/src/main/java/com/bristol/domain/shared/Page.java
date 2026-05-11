package com.bristol.domain.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Domain representation of a page of results.
 * Framework-agnostic pagination support.
 *
 * @param <T> The type of content items
 */
@Getter
@AllArgsConstructor
public class Page<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public int getTotalPages() {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) pageSize);
    }

    public boolean isFirst() {
        return pageNumber == 0;
    }

    public boolean isLast() {
        return pageNumber >= getTotalPages() - 1;
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public static <T> Page<T> empty(int pageNumber, int pageSize) {
        return new Page<>(List.of(), pageNumber, pageSize, 0);
    }
}
