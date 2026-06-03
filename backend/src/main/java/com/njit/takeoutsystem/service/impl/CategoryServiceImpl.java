package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.dto.CategoryRequest;
import com.njit.takeoutsystem.mapper.CategoryMapper;
import com.njit.takeoutsystem.service.CategoryService;
import com.njit.takeoutsystem.vo.CategoryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryVO> list() {
        return categoryMapper.findAllWithDishCount();
    }

    @Override
    @Transactional
    public CategoryVO create(CategoryRequest request) {
        CategoryVO category = new CategoryVO();
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryMapper.insert(category);
        return categoryMapper.findById(category.getId());
    }

    @Override
    @Transactional
    public void update(Long id, CategoryRequest request) {
        if (categoryMapper.existsById(id) == 0) {
            throw new BusinessException(404, "分类不存在");
        }

        CategoryVO category = new CategoryVO();
        category.setId(id);
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryMapper.update(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (categoryMapper.existsById(id) == 0) {
            throw new BusinessException(404, "分类不存在");
        }
        if (categoryMapper.countDishes(id) > 0) {
            throw new BusinessException(409, "该分类下已有菜品，不能删除");
        }
        categoryMapper.delete(id);
    }
}
