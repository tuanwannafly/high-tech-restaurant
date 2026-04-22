package com.restaurant.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import com.restaurant.model.Employee;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.TableItem;

/**
 * DataManager — lớp duy nhất mà UI biết đến.
 * Mọi thao tác đều delegate sang ApiClient (gọi REST API BE).
 * Dùng SwingWorker để không block EDT khi gọi API.
 */
public class DataManager {

    private static DataManager instance;

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    private ApiClient api() { return ApiClient.getInstance(); }

    // ═══════════════════════════════════════════════════════
    //  MENU ITEMS
    // ═══════════════════════════════════════════════════════

    public List<MenuItem> getMenuItems() {
        try { return api().getMenuItems(); }
        catch (Exception e) {
            System.err.println("[DataManager] getMenuItems lỗi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Tạo mới, trả về item đã có ID từ BE (hoặc item gốc nếu lỗi) */
    public MenuItem addMenuItem(MenuItem item) {
        try { return api().createMenuItem(item); }
        catch (Exception e) {
            System.err.println("[DataManager] addMenuItem lỗi: " + e.getMessage());
            return item;
        }
    }

    public MenuItem updateMenuItem(MenuItem updated) {
        try { return api().updateMenuItem(updated); }
        catch (Exception e) {
            System.err.println("[DataManager] updateMenuItem lỗi: " + e.getMessage());
            return updated;
        }
    }

    public void deleteMenuItem(String id) {
        try { api().deleteMenuItem(id); }
        catch (Exception e) { System.err.println("[DataManager] deleteMenuItem lỗi: " + e.getMessage()); }
    }

    // ═══════════════════════════════════════════════════════
    //  TABLES
    // ═══════════════════════════════════════════════════════

    public List<TableItem> getTables() {
        try { return api().getTables(); }
        catch (Exception e) {
            System.err.println("[DataManager] getTables lỗi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public TableItem addTable(TableItem table) {
        try { return api().createTable(table); }
        catch (Exception e) {
            System.err.println("[DataManager] addTable lỗi: " + e.getMessage());
            return table;
        }
    }

    public TableItem updateTable(TableItem updated) {
        try { return api().updateTable(updated); }
        catch (Exception e) {
            System.err.println("[DataManager] updateTable lỗi: " + e.getMessage());
            return updated;
        }
    }

    public void deleteTable(String id) {
        try { api().deleteTable(id); }
        catch (Exception e) { System.err.println("[DataManager] deleteTable lỗi: " + e.getMessage()); }
    }

    // ═══════════════════════════════════════════════════════
    //  EMPLOYEES
    // ═══════════════════════════════════════════════════════

    public List<Employee> getEmployees() {
        try { return api().getEmployees(); }
        catch (Exception e) {
            System.err.println("[DataManager] getEmployees lỗi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Tạo nhân viên mới, trả về employee đã được gán ID thực từ BE */
    public Employee addEmployee(Employee emp) {
        try { return api().createEmployee(emp); }
        catch (Exception e) {
            System.err.println("[DataManager] addEmployee lỗi: " + e.getMessage());
            return emp;
        }
    }

    public Employee updateEmployee(Employee updated) {
        try { return api().updateEmployee(updated); }
        catch (Exception e) {
            System.err.println("[DataManager] updateEmployee lỗi: " + e.getMessage());
            return updated;
        }
    }

    public void deleteEmployee(String id) {
        try { api().deleteEmployee(id); }
        catch (Exception e) { System.err.println("[DataManager] deleteEmployee lỗi: " + e.getMessage()); }
    }

    public String generateEmployeeId() {
        return "NV_NEW"; // BE sẽ sinh ID thực
    }

    // ═══════════════════════════════════════════════════════
    //  ORDERS
    // ═══════════════════════════════════════════════════════

    public List<Order> getOrders() {
        try { return api().getOrders(); }
        catch (Exception e) {
            System.err.println("[DataManager] getOrders lỗi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Order addOrder(Order order) {
        try { return api().createOrder(order); }
        catch (Exception e) {
            System.err.println("[DataManager] addOrder lỗi: " + e.getMessage());
            return order;
        }
    }

    public Order updateOrder(Order updated) {
        try { return api().updateOrder(updated); }
        catch (Exception e) {
            System.err.println("[DataManager] updateOrder lỗi: " + e.getMessage());
            return updated;
        }
    }

    public void deleteOrder(String id) {
        try { api().deleteOrder(id); }
        catch (Exception e) { System.err.println("[DataManager] deleteOrder lỗi: " + e.getMessage()); }
    }

    // ═══════════════════════════════════════════════════════
    //  Async helper: chạy task trong background, sau đó gọi callback trên EDT
    // ═══════════════════════════════════════════════════════

    public static void runAsync(Runnable task, Runnable onDone) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { task.run(); return null; }
            @Override protected void done() { if (onDone != null) onDone.run(); }
        }.execute();
    }

    // ═══════════════════════════════════════════════════════
    //  DASHBOARD STATS
    // ═══════════════════════════════════════════════════════

    public double getTodayRevenue() {
        return getOrders().stream()
                .filter(o -> o.getStatus() == Order.Status.HOAN_THANH)
                .mapToDouble(Order::getTotalAmount).sum();
    }

    public long getTodayOrderCount() {
        return getOrders().stream()
                .filter(o -> o.getStatus() != Order.Status.DA_HUY).count();
    }

    public long getServingTableCount() {
        return getTables().stream()
                .filter(t -> t.getStatus() == TableItem.Status.BAN).count();
    }

    public long getTotalMenuItemsSold() {
        return getOrders().stream()
                .filter(o -> o.getStatus() == Order.Status.HOAN_THANH)
                .flatMap(o -> o.getItems().stream())
                .mapToLong(Order.OrderItem::getQuantity).sum();
    }
}
