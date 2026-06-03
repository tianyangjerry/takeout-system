package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderStatusLogMapper {

    @Insert("""
            INSERT INTO order_status_log (
                order_id, status, status_text, operator_id, operator_role, remark
            )
            VALUES (
                #{orderId}, #{status}, #{statusText}, #{operatorId}, #{operatorRole}, #{remark}
            )
            """)
    int insert(OrderStatusLog log);

    @Select("""
            SELECT *
            FROM order_status_log
            WHERE order_id = #{orderId}
            ORDER BY id ASC
            """)
    List<OrderStatusLog> findByOrderId(Long orderId);
}
