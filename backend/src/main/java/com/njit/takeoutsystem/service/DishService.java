package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.DishRequest;
import com.njit.takeoutsystem.dto.DishQuery;
import com.njit.takeoutsystem.vo.DishVO;

import java.util.List;

public interface DishService {
    PageResult<DishVO> list(DishQuery query);

    DishVO detail(Long id);

    List<DishVO> top(int limit);

    List<DishVO> recommend(int limit);

    Long create(DishRequest request);

    void update(Long id, DishRequest request);

    void updateStatus(Long id, Integer status);

    void delete(Long id);
}
