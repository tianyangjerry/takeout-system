package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.dto.OrderQuery;
import com.njit.takeoutsystem.entity.Order;
import com.njit.takeoutsystem.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("""
            INSERT INTO orders (
                order_no, user_id, total_amount, receiver_name, receiver_phone,
                receiver_address, remark, status
            )
            VALUES (
                #{orderNo}, #{userId}, #{totalAmount}, #{receiverName}, #{receiverPhone},
                #{receiverAddress}, #{remark}, #{status}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("""
            INSERT INTO order_item (order_id, dish_id, dish_name, dish_price, quantity, subtotal)
            VALUES (#{orderId}, #{dishId}, #{dishName}, #{dishPrice}, #{quantity}, #{subtotal})
            """)
    int insertOrderItem(OrderItem item);

    @Select("""
            <script>
            SELECT o.*, u.username,
                   (SELECT COALESCE(SUM(oi.quantity), 0) FROM order_item oi WHERE oi.order_id = o.id) AS item_count
            FROM orders o
            LEFT JOIN user u ON u.id = o.user_id
            <where>
                <if test="userId != null">AND o.user_id = #{userId}</if>
                <if test="status != null and status != ''">AND o.status = #{status}</if>
                <if test="keyword != null and keyword != ''">
                    AND (
                        o.order_no LIKE CONCAT('%', #{keyword}, '%')
                        OR u.username LIKE CONCAT('%', #{keyword}, '%')
                        OR o.receiver_name LIKE CONCAT('%', #{keyword}, '%')
                        OR o.receiver_phone LIKE CONCAT('%', #{keyword}, '%')
                    )
                </if>
                <if test="startDate != null and startDate != ''">AND DATE(o.created_time) &gt;= #{startDate}</if>
                <if test="endDate != null and endDate != ''">AND DATE(o.created_time) &lt;= #{endDate}</if>
            </where>
            ORDER BY o.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Order> findPage(OrderQuery query);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM orders o
            LEFT JOIN user u ON u.id = o.user_id
            <where>
                <if test="userId != null">AND o.user_id = #{userId}</if>
                <if test="status != null and status != ''">AND o.status = #{status}</if>
                <if test="keyword != null and keyword != ''">
                    AND (
                        o.order_no LIKE CONCAT('%', #{keyword}, '%')
                        OR u.username LIKE CONCAT('%', #{keyword}, '%')
                        OR o.receiver_name LIKE CONCAT('%', #{keyword}, '%')
                        OR o.receiver_phone LIKE CONCAT('%', #{keyword}, '%')
                    )
                </if>
                <if test="startDate != null and startDate != ''">AND DATE(o.created_time) &gt;= #{startDate}</if>
                <if test="endDate != null and endDate != ''">AND DATE(o.created_time) &lt;= #{endDate}</if>
            </where>
            </script>
            """)
    long count(OrderQuery query);

    @Select("""
            SELECT o.*, u.username,
                   (SELECT COALESCE(SUM(oi.quantity), 0) FROM order_item oi WHERE oi.order_id = o.id) AS item_count
            FROM orders o
            LEFT JOIN user u ON u.id = o.user_id
            WHERE o.id = #{id}
            LIMIT 1
            """)
    Order findById(Long id);

    @Select("""
            SELECT o.*, u.username,
                   (SELECT COALESCE(SUM(oi.quantity), 0) FROM order_item oi WHERE oi.order_id = o.id) AS item_count
            FROM orders o
            LEFT JOIN user u ON u.id = o.user_id
            WHERE o.id = #{id} AND o.user_id = #{userId}
            LIMIT 1
            """)
    Order findUserOrder(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OrderItem> findItems(Long orderId);

    @Update("""
            UPDATE orders
            SET status = #{status},
                cancel_reason = #{cancelReason}
            WHERE id = #{id}
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("cancelReason") String cancelReason
    );
}
