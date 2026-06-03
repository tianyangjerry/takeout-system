package com.njit.takeoutsystem.dto;

import lombok.Data;

@Data
public class OrderQuery {
    private Long userId;
    private String keyword;
    private String status;
    private String startDate;
    private String endDate;
    private Integer page = 1;
    private Integer pageSize = 10;

    public Integer getOffset() {
        return (getSafePage() - 1) * getLimit();
    }

    public Integer getLimit() {
        return pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
    }

    public Integer getSafePage() {
        return page == null || page < 1 ? 1 : page;
    }
}
