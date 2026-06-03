package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.common.PageResult;
import com.njit.takeoutsystem.dto.CancelOrderRequest;
import com.njit.takeoutsystem.dto.CreateOrderRequest;
import com.njit.takeoutsystem.dto.OrderQuery;
import com.njit.takeoutsystem.dto.UpdateOrderStatusRequest;
import com.njit.takeoutsystem.entity.CartItem;
import com.njit.takeoutsystem.entity.Order;
import com.njit.takeoutsystem.entity.OrderItem;
import com.njit.takeoutsystem.entity.OrderStatusLog;
import com.njit.takeoutsystem.mapper.CartMapper;
import com.njit.takeoutsystem.mapper.DishMapper;
import com.njit.takeoutsystem.mapper.OrderMapper;
import com.njit.takeoutsystem.mapper.OrderStatusLogMapper;
import com.njit.takeoutsystem.service.OrderService;
import com.njit.takeoutsystem.vo.CreateOrderVO;
import com.njit.takeoutsystem.vo.OrderDetailVO;
import com.njit.takeoutsystem.vo.OrderItemVO;
import com.njit.takeoutsystem.vo.OrderStatusLabels;
import com.njit.takeoutsystem.vo.OrderSummaryVO;
import com.njit.takeoutsystem.vo.OrderTimelineVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String PENDING = "PENDING";
    private static final String ACCEPTED = "ACCEPTED";
    private static final String COOKING = "COOKING";
    private static final String DELIVERING = "DELIVERING";
    private static final String COMPLETED = "COMPLETED";
    private static final String CANCELLED = "CANCELLED";
    private static final List<String> FLOW = List.of(PENDING, ACCEPTED, COOKING, DELIVERING, COMPLETED);

    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final DishMapper dishMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;

    public OrderServiceImpl(
            CartMapper cartMapper,
            OrderMapper orderMapper,
            DishMapper dishMapper,
            OrderStatusLogMapper orderStatusLogMapper
    ) {
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
        this.dishMapper = dishMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
    }

    @Override
    @Transactional
    public CreateOrderVO create(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartMapper.findItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException(400, "购物车为空");
        }
        validateCartItems(cartItems);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount(cartItems));
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        order.setStatus(PENDING);
        orderMapper.insertOrder(order);
        addStatusLog(order.getId(), PENDING, userId, "CUSTOMER", "提交订单");

        for (CartItem cartItem : cartItems) {
            if (dishMapper.decreaseStock(cartItem.getDishId(), cartItem.getQuantity()) == 0) {
                throw new BusinessException(400, cartItem.getDishName() + " 库存不足");
            }
            dishMapper.increaseSales(cartItem.getDishId(), cartItem.getQuantity());
            orderMapper.insertOrderItem(toOrderItem(order.getId(), cartItem));
        }
        cartMapper.clearByUserId(userId);

        CreateOrderVO vo = new CreateOrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        return vo;
    }

    @Override
    public PageResult<OrderSummaryVO> listMy(Long userId, OrderQuery query) {
        query.setUserId(userId);
        List<OrderSummaryVO> records = orderMapper.findPage(query).stream()
                .map(OrderSummaryVO::from)
                .toList();
        return PageResult.of(records, orderMapper.count(query), query.getSafePage(), query.getLimit());
    }

    @Override
    public OrderDetailVO detail(Long userId, String role, Long id) {
        Order order = "ADMIN".equals(role) ? orderMapper.findById(id) : orderMapper.findUserOrder(id, userId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return toDetail(order);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long id, CancelOrderRequest request) {
        Order order = orderMapper.findUserOrder(id, userId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!PENDING.equals(order.getStatus())) {
            throw new BusinessException(400, "只有待接单订单可以取消");
        }
        restoreStockAndSales(order.getId());
        orderMapper.updateStatus(id, CANCELLED, safeCancelReason(request));
        addStatusLog(order.getId(), CANCELLED, userId, "CUSTOMER", safeCancelReason(request));
    }

    @Override
    public PageResult<OrderSummaryVO> adminList(OrderQuery query) {
        List<OrderSummaryVO> records = orderMapper.findPage(query).stream()
                .map(OrderSummaryVO::from)
                .toList();
        return PageResult.of(records, orderMapper.count(query), query.getSafePage(), query.getLimit());
    }

    @Override
    @Transactional
    public void adminUpdateStatus(Long adminId, Long id, UpdateOrderStatusRequest request) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        String next = nextStatus(order.getStatus());
        if (next == null) {
            throw new BusinessException(400, "当前订单状态不可流转");
        }
        if (!next.equals(request.getStatus())) {
            throw new BusinessException(400, "订单状态必须按流程流转");
        }
        orderMapper.updateStatus(id, request.getStatus(), null);
        addStatusLog(order.getId(), request.getStatus(), adminId, "ADMIN", "管理员修改订单状态");
    }

    private void validateCartItems(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            if (item.getStatus() == null || item.getStatus() != 1) {
                throw new BusinessException(400, item.getDishName() + " 已下架");
            }
            if (item.getStock() == null || item.getStock() < item.getQuantity()) {
                throw new BusinessException(400, item.getDishName() + " 库存不足");
            }
        }
    }

    private BigDecimal totalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItem toOrderItem(Long orderId, CartItem cartItem) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setDishId(cartItem.getDishId());
        item.setDishName(cartItem.getDishName());
        item.setDishPrice(cartItem.getPrice());
        item.setQuantity(cartItem.getQuantity());
        item.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return item;
    }

    private OrderDetailVO toDetail(Order order) {
        List<OrderItemVO> items = orderMapper.findItems(order.getId()).stream()
                .map(OrderItemVO::from)
                .toList();
        return OrderDetailVO.from(order, items, buildTimeline(order, orderStatusLogMapper.findByOrderId(order.getId())));
    }

    private List<OrderTimelineVO> buildTimeline(Order order, List<OrderStatusLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return fallbackTimeline(order);
        }
        if (CANCELLED.equals(order.getStatus())) {
            return cancelledTimeline(logs);
        }
        return flowTimeline(order, logs);
    }

    private List<OrderTimelineVO> flowTimeline(Order order, List<OrderStatusLog> logs) {
        int currentIndex = FLOW.indexOf(order.getStatus());
        List<OrderTimelineVO> timeline = new ArrayList<>();
        for (String status : FLOW) {
            OrderStatusLog log = findLog(logs, status);
            timeline.add(new OrderTimelineVO(
                    status,
                    labelForTimeline(status),
                    currentIndex >= 0 && FLOW.indexOf(status) <= currentIndex,
                    log == null ? null : log.getCreatedTime()
            ));
        }
        return timeline;
    }

    private List<OrderTimelineVO> cancelledTimeline(List<OrderStatusLog> logs) {
        List<OrderTimelineVO> timeline = new ArrayList<>();
        OrderStatusLog pending = findLog(logs, PENDING);
        OrderStatusLog cancelled = findLog(logs, CANCELLED);
        timeline.add(new OrderTimelineVO(PENDING, labelForTimeline(PENDING), true, pending == null ? null : pending.getCreatedTime()));
        timeline.add(new OrderTimelineVO(CANCELLED, labelForTimeline(CANCELLED), true, cancelled == null ? null : cancelled.getCreatedTime()));
        return timeline;
    }

    private List<OrderTimelineVO> fallbackTimeline(Order order) {
        List<OrderTimelineVO> timeline = new ArrayList<>();
        if (CANCELLED.equals(order.getStatus())) {
            timeline.add(new OrderTimelineVO(PENDING, labelForTimeline(PENDING), true, order.getCreatedTime()));
            timeline.add(new OrderTimelineVO(CANCELLED, labelForTimeline(CANCELLED), true, order.getUpdatedTime()));
            return timeline;
        }
        int currentIndex = FLOW.indexOf(order.getStatus());
        for (int i = 0; i < FLOW.size(); i++) {
            String status = FLOW.get(i);
            boolean active = i <= currentIndex;
            LocalDateTime time = null;
            if (i == 0 && active) {
                time = order.getCreatedTime();
            } else if (i == currentIndex && active) {
                time = order.getUpdatedTime();
            }
            timeline.add(new OrderTimelineVO(status, labelForTimeline(status), active, time));
        }
        return timeline;
    }

    private OrderStatusLog findLog(List<OrderStatusLog> logs, String status) {
        return logs.stream()
                .filter(log -> status.equals(log.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private String labelForTimeline(String status) {
        return switch (status) {
            case PENDING -> "提交订单";
            case ACCEPTED -> "商家接单";
            case COOKING -> "制作中";
            case DELIVERING -> "配送中";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
            default -> OrderStatusLabels.text(status);
        };
    }

    private void restoreStockAndSales(Long orderId) {
        for (OrderItem item : orderMapper.findItems(orderId)) {
            dishMapper.increaseStock(item.getDishId(), item.getQuantity());
            dishMapper.decreaseSales(item.getDishId(), item.getQuantity());
        }
    }

    private String safeCancelReason(CancelOrderRequest request) {
        if (request == null || request.getReason() == null || request.getReason().isBlank()) {
            return "用户取消订单";
        }
        return request.getReason();
    }

    private void addStatusLog(Long orderId, String status, Long operatorId, String operatorRole, String remark) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setStatus(status);
        log.setStatusText(OrderStatusLabels.text(status));
        log.setOperatorId(operatorId);
        log.setOperatorRole(operatorRole);
        log.setRemark(remark);
        orderStatusLogMapper.insert(log);
    }

    private String nextStatus(String status) {
        int index = FLOW.indexOf(status);
        return index >= 0 && index < FLOW.size() - 1 ? FLOW.get(index + 1) : null;
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "SO" + time + random;
    }
}
