package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.vo.CategorySalesStatsVO;
import com.njit.takeoutsystem.vo.OverviewStatsVO;
import com.njit.takeoutsystem.vo.SalesTrendItemVO;
import com.njit.takeoutsystem.vo.StockAlertItemVO;
import com.njit.takeoutsystem.vo.TopDishStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatisticsMapper {

    @Select("SELECT CURDATE()")
    LocalDate currentDate();

    @Select("""
            SELECT
                (SELECT COUNT(*) FROM orders WHERE DATE(created_time) = CURDATE() AND status != 'CANCELLED') AS today_order_count,
                (SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(created_time) = CURDATE() AND status != 'CANCELLED') AS today_revenue,
                (SELECT COUNT(*) FROM orders WHERE status = 'PENDING') AS pending_order_count,
                (SELECT COUNT(*) FROM dish WHERE status = 1 AND stock < 10) AS low_stock_dish_count,
                (SELECT COUNT(*) FROM `user`) AS total_user_count,
                (SELECT COUNT(*) FROM dish) AS total_dish_count
            """)
    OverviewStatsVO overview();

    @Select("""
            SELECT
                DATE_FORMAT(created_time, '%Y-%m-%d') AS date,
                COUNT(*) AS order_count,
                COALESCE(SUM(total_amount), 0) AS revenue
            FROM orders
            WHERE status != 'CANCELLED'
              AND DATE(created_time) >= DATE_SUB(CURDATE(), INTERVAL #{daysMinusOne} DAY)
            GROUP BY DATE_FORMAT(created_time, '%Y-%m-%d')
            ORDER BY DATE_FORMAT(created_time, '%Y-%m-%d') ASC
            """)
    List<SalesTrendItemVO> salesTrend(@Param("daysMinusOne") int daysMinusOne);

    @Select("""
            SELECT
                oi.dish_id AS dish_id,
                oi.dish_name AS dish_name,
                COALESCE(SUM(oi.quantity), 0) AS sales,
                COALESCE(SUM(oi.subtotal), 0) AS revenue
            FROM order_item oi
            INNER JOIN orders o ON o.id = oi.order_id
            WHERE o.status != 'CANCELLED'
            GROUP BY oi.dish_id, oi.dish_name
            ORDER BY sales DESC, revenue DESC, oi.dish_id ASC
            LIMIT #{limit}
            """)
    List<TopDishStatsVO> topDishes(@Param("limit") int limit);

    @Select("""
            SELECT
                c.id AS category_id,
                c.name AS category_name,
                COALESCE(SUM(CASE WHEN o.id IS NOT NULL THEN oi.quantity ELSE 0 END), 0) AS sales,
                COALESCE(SUM(CASE WHEN o.id IS NOT NULL THEN oi.subtotal ELSE 0 END), 0) AS revenue
            FROM category c
            LEFT JOIN dish d ON d.category_id = c.id
            LEFT JOIN order_item oi ON oi.dish_id = d.id
            LEFT JOIN orders o ON o.id = oi.order_id AND o.status != 'CANCELLED'
            GROUP BY c.id, c.name, c.sort_order
            HAVING sales > 0 OR revenue > 0
            ORDER BY revenue DESC, sales DESC, c.sort_order ASC, c.id ASC
            """)
    List<CategorySalesStatsVO> categorySales();

    @Select("""
            SELECT
                id AS dish_id,
                name AS dish_name,
                stock,
                #{threshold} AS threshold,
                CASE WHEN stock = 0 THEN 'SOLD_OUT' ELSE 'LOW_STOCK' END AS status,
                CASE WHEN stock = 0 THEN '已售罄' ELSE '库存不足' END AS status_text
            FROM dish
            WHERE status = 1 AND stock >= 0 AND stock < #{threshold}
            ORDER BY stock ASC, id ASC
            """)
    List<StockAlertItemVO> stockAlert(@Param("threshold") int threshold);
}
