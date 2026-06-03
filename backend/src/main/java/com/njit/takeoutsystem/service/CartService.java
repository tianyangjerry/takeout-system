package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.dto.AddCartItemRequest;
import com.njit.takeoutsystem.dto.UpdateCartItemRequest;
import com.njit.takeoutsystem.vo.CartVO;

public interface CartService {
    CartVO get(Long userId);

    void addItem(Long userId, AddCartItemRequest request);

    void updateItem(Long userId, Long itemId, UpdateCartItemRequest request);

    void removeItem(Long userId, Long itemId);

    void clear(Long userId);
}
