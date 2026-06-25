package com.njit.takeoutsystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesTrendItemVO {
    private String date;
    private Long orderCount;
    private BigDecimal revenue;
}
