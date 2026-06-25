package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.vo.CategorySalesStatsVO;
import com.njit.takeoutsystem.vo.OverviewStatsVO;
import com.njit.takeoutsystem.vo.SalesTrendItemVO;
import com.njit.takeoutsystem.vo.StockAlertItemVO;
import com.njit.takeoutsystem.vo.TopDishStatsVO;

import java.util.List;

public interface StatisticsService {
    OverviewStatsVO overview();

    List<SalesTrendItemVO> salesTrend(int days);

    List<TopDishStatsVO> topDishes(int limit);

    List<CategorySalesStatsVO> categorySales();

    List<StockAlertItemVO> stockAlert(int threshold);
}
