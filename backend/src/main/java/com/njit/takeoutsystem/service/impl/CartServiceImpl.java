package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.dto.AddCartItemRequest;
import com.njit.takeoutsystem.dto.UpdateCartItemRequest;
import com.njit.takeoutsystem.entity.Cart;
import com.njit.takeoutsystem.entity.CartItem;
import com.njit.takeoutsystem.entity.Dish;
import com.njit.takeoutsystem.mapper.CartMapper;
import com.njit.takeoutsystem.mapper.DishMapper;
import com.njit.takeoutsystem.service.CartService;
import com.njit.takeoutsystem.vo.CartItemVO;
import com.njit.takeoutsystem.vo.CartVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final DishMapper dishMapper;

    public CartServiceImpl(CartMapper cartMapper, DishMapper dishMapper) {
        this.cartMapper = cartMapper;
        this.dishMapper = dishMapper;
    }

    @Override
    public CartVO get(Long userId) {
        List<CartItemVO> items = cartMapper.findItemsByUserId(userId).stream()
                .map(CartItemVO::from)
                .toList();
        return CartVO.from(items);
    }

    @Override
    @Transactional
    public void addItem(Long userId, AddCartItemRequest request) {
        Dish dish = requireAvailableDish(request.getDishId());
        Long cartId = ensureCart(userId);
        CartItem current = cartMapper.findItemByCartAndDish(cartId, request.getDishId());
        int nextQuantity = request.getQuantity() + (current == null ? 0 : current.getQuantity());
        checkStock(dish, nextQuantity);
        if (current == null) {
            cartMapper.insertItem(cartId, request.getDishId(), request.getQuantity());
            return;
        }
        cartMapper.updateItemQuantity(current.getId(), nextQuantity);
    }

    @Override
    @Transactional
    public void updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem item = requireUserItem(userId, itemId);
        Dish dish = requireAvailableDish(item.getDishId());
        checkStock(dish, request.getQuantity());
        cartMapper.updateItemQuantity(itemId, request.getQuantity());
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long itemId) {
        if (cartMapper.deleteUserItem(userId, itemId) == 0) {
            throw new BusinessException(404, "购物车项不存在");
        }
    }

    @Override
    @Transactional
    public void clear(Long userId) {
        cartMapper.clearByUserId(userId);
    }

    private Long ensureCart(Long userId) {
        Long cartId = cartMapper.findCartIdByUserId(userId);
        if (cartId != null) {
            return cartId;
        }
        Cart cart = new Cart();
        cart.setUserId(userId);
        cartMapper.insertCart(cart);
        return cart.getId();
    }

    private CartItem requireUserItem(Long userId, Long itemId) {
        CartItem item = cartMapper.findUserItem(userId, itemId);
        if (item == null) {
            throw new BusinessException(404, "购物车项不存在");
        }
        return item;
    }

    private Dish requireAvailableDish(Long dishId) {
        Dish dish = dishMapper.findById(dishId);
        if (dish == null) {
            throw new BusinessException(404, "菜品不存在");
        }
        if (dish.getStatus() == null || dish.getStatus() != 1) {
            throw new BusinessException(400, "菜品已下架");
        }
        return dish;
    }

    private void checkStock(Dish dish, int quantity) {
        if (dish.getStock() == null || dish.getStock() < quantity) {
            throw new BusinessException(400, "库存不足");
        }
    }
}
