package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.service.StatisticsService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.CategorySalesStatsVO;
import com.njit.takeoutsystem.vo.OverviewStatsVO;
import com.njit.takeoutsystem.vo.SalesTrendItemVO;
import com.njit.takeoutsystem.vo.StockAlertItemVO;
import com.njit.takeoutsystem.vo.TopDishStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final CurrentUserUtil currentUserUtil;

    public StatisticsController(StatisticsService statisticsService, CurrentUserUtil currentUserUtil) {
        this.statisticsService = statisticsService;
        this.currentUserUtil = currentUserUtil;
    }

    @GetMapping("/overview")
    public ApiResponse<OverviewStatsVO> overview(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(statisticsService.overview());
    }

    @GetMapping("/sales-trend")
    public ApiResponse<List<SalesTrendItemVO>> salesTrend(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(defaultValue = "7") int days
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(statisticsService.salesTrend(days));
    }

    @GetMapping("/top-dishes")
    public ApiResponse<List<TopDishStatsVO>> topDishes(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(defaultValue = "5") int limit
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(statisticsService.topDishes(limit));
    }

    @GetMapping("/category-sales")
    public ApiResponse<List<CategorySalesStatsVO>> categorySales(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(statisticsService.categorySales());
    }

    @GetMapping("/stock-alert")
    public ApiResponse<List<StockAlertItemVO>> stockAlert(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(defaultValue = "10") int threshold
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(statisticsService.stockAlert(threshold));
    }
}
