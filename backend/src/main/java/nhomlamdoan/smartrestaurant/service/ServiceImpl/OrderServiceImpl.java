package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhomlamdoan.smartrestaurant.domain.MenuItem;
import nhomlamdoan.smartrestaurant.domain.Order;
import nhomlamdoan.smartrestaurant.domain.OrderItem;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.RestaurantTable;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.constant.OrderItemStatus;
import nhomlamdoan.smartrestaurant.domain.constant.OrderStatus;
import nhomlamdoan.smartrestaurant.domain.constant.TableStatus;
import nhomlamdoan.smartrestaurant.domain.request.order.ReqOrder;
import nhomlamdoan.smartrestaurant.domain.response.order.ResOrder;
import nhomlamdoan.smartrestaurant.repository.MenuItemRepository;
import nhomlamdoan.smartrestaurant.repository.OrderRepository;
import nhomlamdoan.smartrestaurant.repository.RestaurantTableRepository;
import nhomlamdoan.smartrestaurant.repository.UserRepository;
import nhomlamdoan.smartrestaurant.service.OrderService;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            RestaurantTableRepository tableRepository,
                            MenuItemRepository menuItemRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------ public API

    @Override
    @Transactional
    public ResOrder createOrder(ReqOrder req, Restaurant restaurant, User waiter) {
        RestaurantTable table = tableRepository.findById(req.getTableId())
                .orElseThrow(() -> new RuntimeException(
                        "Bàn không tồn tại với id = " + req.getTableId()));

        Order order = new Order();
        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setWaiter(waiter);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setTotalAmount(BigDecimal.ZERO);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (req.getItems() != null) {
            for (ReqOrder.ReqOrderItem reqItem : req.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(reqItem.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException(
                                "MenuItem không tồn tại với id = " + reqItem.getMenuItemId()));

                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setMenuItem(menuItem);
                oi.setQuantity(reqItem.getQuantity());
                oi.setPrice(menuItem.getPrice());
                oi.setNote(reqItem.getNote());
                oi.setStatus(OrderItemStatus.PENDING);
                orderItems.add(oi);

                total = total.add(menuItem.getPrice()
                        .multiply(BigDecimal.valueOf(reqItem.getQuantity())));
            }
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);

        // Khi có đơn hàng → đánh dấu bàn đang sử dụng
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        return toResOrder(orderRepository.save(order));
    }

    @Override
    @Transactional
    public ResOrder updateOrderWithOwnershipCheck(Long id, ReqOrder req, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        Order order = findAndVerifyOwnership(id, restaurant);

        if (req.getStatus() != null) {
            OrderStatus newStatus = parseStatus(req.getStatus(), order.getStatus());
            order.setStatus(newStatus);

            // Khi hoàn thành hoặc hủy → giải phóng bàn
            if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED) {
                RestaurantTable table = order.getTable();
                if (table != null) {
                    table.setStatus(TableStatus.AVAILABLE);
                    tableRepository.save(table);
                }
            }
        }

        return toResOrder(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrderWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        Order order = findAndVerifyOwnership(id, restaurant);

        // Giải phóng bàn khi xóa đơn hàng đang mở
        RestaurantTable table = order.getTable();
        if (table != null && table.getStatus() == TableStatus.OCCUPIED) {
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        orderRepository.deleteById(id);
    }

    @Override
    public List<ResOrder> getAllOrders(Restaurant restaurant) {
        return orderRepository.findByRestaurant(restaurant)
                .stream()
                .map(this::toResOrder)
                .collect(Collectors.toList());
    }

    @Override
    public ResOrder toResOrder(Order order) {
        ResOrder res = new ResOrder();
        res.setId(order.getOrderId());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        res.setCreatedAt(order.getCreatedAt());

        if (order.getTable() != null) {
            res.setTableId(order.getTable().getTableId());
            res.setTableNumber(order.getTable().getTableNumber());
        }

        if (order.getOrderItems() != null) {
            List<ResOrder.ResOrderItem> items = order.getOrderItems().stream().map(oi -> {
                ResOrder.ResOrderItem ri = new ResOrder.ResOrderItem();
                ri.setOrderItemId(oi.getOrderItemId());
                ri.setQuantity(oi.getQuantity());
                ri.setPrice(oi.getPrice());
                ri.setNote(oi.getNote());
                ri.setStatus(oi.getStatus() != null ? oi.getStatus().name() : null);
                if (oi.getMenuItem() != null) {
                    ri.setMenuItemId(oi.getMenuItem().getItemId());
                    ri.setMenuItemName(oi.getMenuItem().getName());
                }
                return ri;
            }).collect(Collectors.toList());
            res.setItems(items);
        }

        return res;
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Tìm đơn hàng theo id và xác minh đơn hàng thuộc nhà hàng hiện tại.
     */
    private Order findAndVerifyOwnership(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException(
                        "Đơn hàng không tồn tại với id = " + id));

        if (!order.getRestaurant().getRestaurantId().equals(restaurant.getRestaurantId())) {
            throw new PermissionException("Đơn hàng không thuộc nhà hàng của bạn.");
        }
        return order;
    }

    private OrderStatus parseStatus(String statusStr, OrderStatus defaultStatus) {
        if (statusStr == null) return defaultStatus;
        try {
            return OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultStatus;
        }
    }
}
