package com.njit.takeoutsystem.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewStatsVO {
    private Long todayOrderCount;
    private BigDecimal todayRevenue;
    private Long pendingOrderCount;
    private Long lowStockDishCount;
    private Long totalUserCount;
    private Long totalDishCount;
}
