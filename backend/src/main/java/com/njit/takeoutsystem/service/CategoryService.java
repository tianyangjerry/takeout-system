package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.dto.CategoryRequest;
import com.njit.takeoutsystem.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<CategoryVO> list();

    CategoryVO create(CategoryRequest request);

    void update(Long id, CategoryRequest request);

    void delete(Long id);
}
