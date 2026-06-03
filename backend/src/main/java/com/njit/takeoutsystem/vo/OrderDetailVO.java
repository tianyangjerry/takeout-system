package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private String status;
    private String statusText;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer itemCount;
    private List<OrderItemVO> items;
    private List<OrderTimelineVO> timeline;

    public static OrderDetailVO from(Order order, List<OrderItemVO> items, List<OrderTimelineVO> timeline) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setUsername(order.getUsername());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderStatusLabels.text(order.getStatus()));
        vo.setCreatedTime(order.getCreatedTime());
        vo.setUpdatedTime(order.getUpdatedTime());
        vo.setItemCount(order.getItemCount());
        vo.setItems(items);
        vo.setTimeline(timeline);
        return vo;
    }
}
