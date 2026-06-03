package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.dto.AddCartItemRequest;
import com.njit.takeoutsystem.dto.UpdateCartItemRequest;
import com.njit.takeoutsystem.entity.User;
import com.njit.takeoutsystem.service.CartService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.CartVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final CurrentUserUtil currentUserUtil;

    public CartController(CartService cartService, CurrentUserUtil currentUserUtil) {
        this.cartService = cartService;
        this.currentUserUtil = currentUserUtil;
    }

    @GetMapping
    public ApiResponse<CartVO> get(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        return ApiResponse.success(cartService.get(user.getId()));
    }

    @PostMapping("/items")
    public ApiResponse<Void> addItem(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        cartService.addItem(user.getId(), request);
        return ApiResponse.success("加入购物车成功", null);
    }

    @PutMapping("/items/{id}")
    public ApiResponse<Void> updateItem(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        cartService.updateItem(user.getId(), id, request);
        return ApiResponse.success("修改成功", null);
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> removeItem(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        cartService.removeItem(user.getId(), id);
        return ApiResponse.success("删除成功", null);
    }

    @DeleteMapping
    public ApiResponse<Void> clear(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User user = currentUserUtil.requireLogin(authorizationHeader);
        cartService.clear(user.getId());
        return ApiResponse.success("清空成功", null);
    }
}
