package com.njit.takeoutsystem.dto;

import lombok.Data;

@Data
public class DishQuery {
    private String keyword;
    private Long categoryId;
    private Integer status = 1;
    private String sort;
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePage - 1) * safePageSize;
    }

    public Integer getLimit() {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }

    public Integer getSafePage() {
        return page == null || page < 1 ? 1 : page;
    }

    public Integer getQueryStatus() {
        return status != null && status < 0 ? null : status;
    }
}
