package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.service.CategoryService;
import com.njit.takeoutsystem.vo.CategoryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.success(categoryService.list());
    }
}
