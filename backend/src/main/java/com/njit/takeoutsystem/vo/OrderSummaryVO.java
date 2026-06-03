package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderSummaryVO {
    private Long id;
    private String orderNo;
    private String username;
    private BigDecimal totalAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String status;
    private String statusText;
    private LocalDateTime createdTime;
    private Integer itemCount;

    public static OrderSummaryVO from(Order order) {
        OrderSummaryVO vo = new OrderSummaryVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUsername(order.getUsername());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderStatusLabels.text(order.getStatus()));
        vo.setCreatedTime(order.getCreatedTime());
        vo.setItemCount(order.getItemCount());
        return vo;
    }
}
