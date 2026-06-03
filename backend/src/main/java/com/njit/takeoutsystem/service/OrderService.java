package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.CancelOrderRequest;
import com.njit.takeoutsystem.dto.CreateOrderRequest;
import com.njit.takeoutsystem.dto.OrderQuery;
import com.njit.takeoutsystem.dto.UpdateOrderStatusRequest;
import com.njit.takeoutsystem.vo.CreateOrderVO;
import com.njit.takeoutsystem.vo.OrderDetailVO;
import com.njit.takeoutsystem.vo.OrderSummaryVO;

public interface OrderService {
    CreateOrderVO create(Long userId, CreateOrderRequest request);

    PageResult<OrderSummaryVO> listMy(Long userId, OrderQuery query);

    OrderDetailVO detail(Long userId, String role, Long id);

    void cancel(Long userId, Long id, CancelOrderRequest request);

    PageResult<OrderSummaryVO> adminList(OrderQuery query);

    void adminUpdateStatus(Long adminId, Long id, UpdateOrderStatusRequest request);
}
