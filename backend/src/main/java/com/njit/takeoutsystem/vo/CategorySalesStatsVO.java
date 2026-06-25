package com.njit.takeoutsystem.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategorySalesStatsVO {
    private Long categoryId;
    private String categoryName;
    private Long sales;
    private BigDecimal revenue;
    private BigDecimal percent;
}
