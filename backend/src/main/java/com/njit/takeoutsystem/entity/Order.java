package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
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
    private String cancelReason;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer itemCount;
}
