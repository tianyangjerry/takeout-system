package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.DishQuery;
import com.njit.takeoutsystem.service.DishService;
import com.njit.takeoutsystem.vo.DishVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ApiResponse<PageResult<DishVO>> list(DishQuery query) {
        return ApiResponse.success(dishService.list(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<DishVO> detail(@PathVariable Long id) {
        return ApiResponse.success(dishService.detail(id));
    }

    @GetMapping("/top")
    public ApiResponse<List<DishVO>> top(@RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(dishService.top(limit));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<DishVO>> recommend(@RequestParam(defaultValue = "6") int limit) {
        return ApiResponse.success(dishService.recommend(limit));
    }
}
