package com.njit.takeoutsystem.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopDishStatsVO {
    private Long dishId;
    private String dishName;
    private Long sales;
    private BigDecimal revenue;
}
