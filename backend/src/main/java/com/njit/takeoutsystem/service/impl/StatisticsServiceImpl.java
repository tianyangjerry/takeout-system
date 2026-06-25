package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.mapper.StatisticsMapper;
import com.njit.takeoutsystem.service.StatisticsService;
import com.njit.takeoutsystem.vo.CategorySalesStatsVO;
import com.njit.takeoutsystem.vo.OverviewStatsVO;
import com.njit.takeoutsystem.vo.SalesTrendItemVO;
import com.njit.takeoutsystem.vo.StockAlertItemVO;
import com.njit.takeoutsystem.vo.TopDishStatsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final int DEFAULT_DAYS = 7;
    private static final int MAX_DAYS = 30;
    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 20;
    private static final int DEFAULT_THRESHOLD = 10;

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public OverviewStatsVO overview() {
        OverviewStatsVO overview = statisticsMapper.overview();
        return overview == null ? new OverviewStatsVO() : overview;
    }

    @Override
    public List<SalesTrendItemVO> salesTrend(int days) {
        int safeDays = safeRange(days, DEFAULT_DAYS, MAX_DAYS);
        Map<String, SalesTrendItemVO> trendByDate = statisticsMapper.salesTrend(safeDays - 1).stream()
                .collect(Collectors.toMap(SalesTrendItemVO::getDate, Function.identity()));
        LocalDate startDate = statisticsMapper.currentDate().minusDays(safeDays - 1L);
        return IntStream.range(0, safeDays)
                .mapToObj(startDate::plusDays)
                .map(LocalDate::toString)
                .map(date -> trendByDate.getOrDefault(date, new SalesTrendItemVO(date, 0L, BigDecimal.ZERO)))
                .toList();
    }

    @Override
    public List<TopDishStatsVO> topDishes(int limit) {
        return statisticsMapper.topDishes(safeRange(limit, DEFAULT_LIMIT, MAX_LIMIT));
    }

    @Override
    public List<CategorySalesStatsVO> categorySales() {
        List<CategorySalesStatsVO> stats = statisticsMapper.categorySales();
        BigDecimal totalRevenue = stats.stream()
                .map(CategorySalesStatsVO::getRevenue)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.forEach(item -> item.setPercent(percent(item.getRevenue(), totalRevenue)));
        return stats;
    }

    @Override
    public List<StockAlertItemVO> stockAlert(int threshold) {
        int safeThreshold = threshold <= 0 ? DEFAULT_THRESHOLD : threshold;
        return statisticsMapper.stockAlert(safeThreshold);
    }

    private int safeRange(int value, int defaultValue, int maxValue) {
        if (value <= 0) {
            return defaultValue;
        }
        return Math.min(value, maxValue);
    }

    private BigDecimal percent(BigDecimal value, BigDecimal total) {
        if (value == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP);
    }
}
