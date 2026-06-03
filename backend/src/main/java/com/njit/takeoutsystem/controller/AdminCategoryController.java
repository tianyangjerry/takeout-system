package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.dto.CategoryRequest;
import com.njit.takeoutsystem.service.CategoryService;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.CategoryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final CurrentUserUtil currentUserUtil;

    public AdminCategoryController(CategoryService categoryService, CurrentUserUtil currentUserUtil) {
        this.categoryService = categoryService;
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping
    public ApiResponse<CategoryVO> create(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CategoryRequest request
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        return ApiResponse.success("新增成功", categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        categoryService.update(id, request);
        return ApiResponse.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        categoryService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
