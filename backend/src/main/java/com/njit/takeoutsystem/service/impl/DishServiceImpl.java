package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.DishRequest;
import com.njit.takeoutsystem.dto.DishQuery;
import com.njit.takeoutsystem.entity.Dish;
import com.njit.takeoutsystem.mapper.CategoryMapper;
import com.njit.takeoutsystem.mapper.DishMapper;
import com.njit.takeoutsystem.service.DishService;
import com.njit.takeoutsystem.vo.DishVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    public DishServiceImpl(DishMapper dishMapper, CategoryMapper categoryMapper) {
        this.dishMapper = dishMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageResult<DishVO> list(DishQuery query) {
        List<DishVO> records = dishMapper.findPage(query).stream()
                .map(DishVO::from)
                .toList();
        long total = dishMapper.count(query);
        return PageResult.of(records, total, query.getSafePage(), query.getLimit());
    }

    @Override
    public DishVO detail(Long id) {
        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new BusinessException(404, "菜品不存在");
        }
        return DishVO.from(dish);
    }

    @Override
    public List<DishVO> top(int limit) {
        return dishMapper.findTop(safeLimit(limit)).stream()
                .map(DishVO::from)
                .toList();
    }

    @Override
    public List<DishVO> recommend(int limit) {
        return dishMapper.findRecommend(safeLimit(limit)).stream()
                .map(this::withRecommendScore)
                .toList();
    }

    @Override
    @Transactional
    public Long create(DishRequest request) {
        checkCategory(request.getCategoryId());
        Dish dish = toDish(new Dish(), request);
        dishMapper.insert(dish);
        return dish.getId();
    }

    @Override
    @Transactional
    public void update(Long id, DishRequest request) {
        Dish current = dishMapper.findById(id);
        if (current == null) {
            throw new BusinessException(404, "菜品不存在");
        }
        checkCategory(request.getCategoryId());
        Dish dish = toDish(current, request);
        dish.setId(id);
        dishMapper.update(dish);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "菜品状态只能是 0 或 1");
        }
        if (dishMapper.findById(id) == null) {
            throw new BusinessException(404, "菜品不存在");
        }
        dishMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (dishMapper.findById(id) == null) {
            throw new BusinessException(404, "菜品不存在");
        }
        dishMapper.delete(id);
    }

    private DishVO withRecommendScore(Dish dish) {
        DishVO vo = DishVO.from(dish);
        BigDecimal salesScore = BigDecimal.valueOf(dish.getSales() == null ? 0 : dish.getSales()).multiply(BigDecimal.valueOf(0.5));
        BigDecimal ratingScore = (dish.getRating() == null ? BigDecimal.ZERO : dish.getRating()).multiply(BigDecimal.valueOf(20));
        vo.setRecommendScore(salesScore.add(ratingScore));
        return vo;
    }

    private int safeLimit(int limit) {
        if (limit <= 0) {
            return 5;
        }
        return Math.min(limit, 20);
    }

    private void checkCategory(Long categoryId) {
        if (categoryId == null || categoryMapper.existsById(categoryId) == 0) {
            throw new BusinessException(404, "分类不存在");
        }
    }

    private Dish toDish(Dish dish, DishRequest request) {
        dish.setCategoryId(request.getCategoryId());
        dish.setName(request.getName());
        dish.setPrice(request.getPrice());
        dish.setStock(request.getStock());
        dish.setImageUrl(request.getImageUrl());
        dish.setDescription(request.getDescription());
        dish.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return dish;
    }
}
