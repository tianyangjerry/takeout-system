package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.dto.DishRequest;
import com.njit.takeoutsystem.service.DishService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dishes")
public class AdminDishController {
    private final DishService dishService;
    private final CurrentUserUtil currentUserUtil;

    public AdminDishController(DishService dishService, CurrentUserUtil currentUserUtil) {
        this.dishService = dishService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> create(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody DishRequest request
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success("新增成功", Map.of("id", dishService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody DishRequest request
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        dishService.update(id, request);
        return ApiResponse.success("修改成功", null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @RequestBody StatusRequest request
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        dishService.updateStatus(id, request.getStatus());
        return ApiResponse.success("状态修改成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        dishService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @Data
    public static class StatusRequest {
        private Integer status;
    }
}
