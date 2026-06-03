package com.njit.takeoutsystem.vo;

public final class OrderStatusLabels {
    private OrderStatusLabels() {
    }

    public static String text(String status) {
        return switch (status) {
            case "PENDING" -> "待接单";
            case "ACCEPTED" -> "已接单";
            case "COOKING" -> "制作中";
            case "DELIVERING" -> "配送中";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }
}
