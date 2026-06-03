package com.njit.takeoutsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Long pages;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int pageSize) {
        long pages = pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(records, total, page, pageSize, pages);
    }
}
