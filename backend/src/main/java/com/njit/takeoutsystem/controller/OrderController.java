package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.CancelOrderRequest;
import com.njit.takeoutsystem.dto.CreateOrderRequest;
import com.njit.takeoutsystem.dto.OrderQuery;
import com.njit.takeoutsystem.entity.User;
import com.njit.takeoutsystem.service.OrderService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.CreateOrderVO;
import com.njit.takeoutsystem.vo.OrderDetailVO;
import com.njit.takeoutsystem.vo.OrderSummaryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final CurrentUserUtil currentUserUtil;

    public OrderController(OrderService orderService, CurrentUserUtil currentUserUtil) {
        this.orderService = orderService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping
    public ApiResponse<CreateOrderVO> create(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        return ApiResponse.success("下单成功", orderService.create(user.getId(), request));
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<OrderSummaryVO>> my(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            OrderQuery query
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        return ApiResponse.success(orderService.listMy(user.getId(), query));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        return ApiResponse.success(orderService.detail(user.getId(), user.getRole(), id));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @RequestBody(required = false) CancelOrderRequest request
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        orderService.cancel(user.getId(), id, request);
        return ApiResponse.success("取消成功", null);
    }
}
