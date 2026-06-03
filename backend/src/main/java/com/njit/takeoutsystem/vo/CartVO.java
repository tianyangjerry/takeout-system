package com.njit.takeoutsystem.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private List<CartItemVO> items;
    private BigDecimal totalAmount;
    private Integer totalQuantity;

    public static CartVO from(List<CartItemVO> items) {
        CartVO vo = new CartVO();
        vo.setItems(items);
        vo.setTotalAmount(items.stream()
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setTotalQuantity(items.stream()
                .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
                .sum());
        return vo;
    }
}
