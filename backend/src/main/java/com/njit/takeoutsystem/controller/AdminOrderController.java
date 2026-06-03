package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.OrderQuery;
import com.njit.takeoutsystem.dto.UpdateOrderStatusRequest;
import com.njit.takeoutsystem.entity.User;
import com.njit.takeoutsystem.service.OrderService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.OrderDetailVO;
import com.njit.takeoutsystem.vo.OrderSummaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderService orderService;
    private final CurrentUserUtil currentUserUtil;

    public AdminOrderController(OrderService orderService, CurrentUserUtil currentUserUtil) {
        this.orderService = orderService;
        this.currentUserUtil = currentUserUtil;
    }

    @GetMapping
    public ApiResponse<PageResult<OrderSummaryVO>> list(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            OrderQuery query
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(orderService.adminList(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        User user = currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success(orderService.detail(user.getId(), user.getRole(), id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        User user = currentUserUtil.requireAdmin(authorizationHeader);
        orderService.adminUpdateStatus(user.getId(), id, request);
        return ApiResponse.success("状态修改成功", null);
    }
}
