package com.restaurant.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restaurant.model.Employee;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.TableItem;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Gọi REST API của BE (Spring Boot).
 * Base URL đọc từ biến hệ thống "api.base.url", mặc định http://localhost:8080
 */
public class ApiClient {

    private static ApiClient instance;

    private static final String BASE_URL =
            System.getProperty("api.base.url", "http://localhost:8080");

    private final HttpClient  http;
    private final ObjectMapper mapper;

    private ApiClient() {
        http   = HttpClient.newBuilder()
                           .connectTimeout(Duration.ofSeconds(5))
                           .build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // hỗ trợ LocalDateTime
    }

    public static ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    // ═══════════════════════════════════════════════════════
    //  HTTP helpers
    // ═══════════════════════════════════════════════════════

    private HttpRequest.Builder authorized() {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        String token = AuthManager.getInstance().getAccessToken();
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b;
    }

    private String get(String path) throws IOException, InterruptedException {
        HttpRequest req = authorized().GET().uri(URI.create(BASE_URL + path)).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400) throw new IOException("HTTP " + res.statusCode() + " - " + path);
        return res.body();
    }

    private String post(String path, String body) throws IOException, InterruptedException {
        HttpRequest req = authorized()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(BASE_URL + path)).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400) throw new IOException("HTTP " + res.statusCode() + " - " + path);
        return res.body();
    }

    private String put(String path, String body) throws IOException, InterruptedException {
        HttpRequest req = authorized()
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(BASE_URL + path)).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400) throw new IOException("HTTP " + res.statusCode() + " - " + path);
        return res.body();
    }

    private void delete(String path) throws IOException, InterruptedException {
        HttpRequest req = authorized()
                .DELETE()
                .uri(URI.create(BASE_URL + path)).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400) throw new IOException("HTTP " + res.statusCode() + " - " + path);
    }

    /** Lấy node "data" từ response wrapper { statusCode, data, message } */
    private JsonNode data(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        if (root.has("data")) return root.get("data");
        return root; // fallback nếu BE trả về plain JSON
    }

    // ═══════════════════════════════════════════════════════
    //  AUTH
    // ═══════════════════════════════════════════════════════

    /**
     * @return accessToken nếu thành công, null nếu sai thông tin
     */
    public String login(String username, String password) {
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("username", username);
            body.put("password", password);

            String json = post("/api/v1/auth/login", mapper.writeValueAsString(body));
            JsonNode node = data(json);

            String token = node.path("access_token").asText(null);
            if (token != null && !token.isEmpty()) {
                AuthManager.getInstance().setAccessToken(token);

                JsonNode user = node.path("user");
                AuthManager.getInstance().setUserInfo(
                        user.path("id").asLong(),
                        user.path("name").asText(""),
                        user.path("email").asText(""),
                        user.path("role").path("name").asText("")
                );
            }
            return token;
        } catch (Exception e) {
            System.err.println("[ApiClient] login failed: " + e.getMessage());
            return null;
        }
    }

    public void logout() {
        try {
            post("/api/v1/auth/logout", "{}");
        } catch (Exception ignored) {}
        AuthManager.getInstance().logout();
    }

    // ═══════════════════════════════════════════════════════
    //  MENU ITEMS
    // ═══════════════════════════════════════════════════════

    public List<MenuItem> getMenuItems() {
        List<MenuItem> result = new ArrayList<>();
        try {
            JsonNode arr = data(get("/api/v1/menu-items"));
            if (!arr.isArray()) return result;
            for (JsonNode n : arr) result.add(mapMenuItem(n));
        } catch (Exception e) { System.err.println("[ApiClient] getMenuItems: " + e.getMessage()); }
        return result;
    }

    public MenuItem createMenuItem(MenuItem item) {
        try {
            String json = post("/api/v1/menu-items", toMenuItemJson(item));
            return mapMenuItem(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] createMenuItem: " + e.getMessage()); return item; }
    }

    public MenuItem updateMenuItem(MenuItem item) {
        try {
            String json = put("/api/v1/menu-items/" + item.getId(), toMenuItemJson(item));
            return mapMenuItem(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] updateMenuItem: " + e.getMessage()); return item; }
    }

    public void deleteMenuItem(String id) {
        try { delete("/api/v1/menu-items/" + id); }
        catch (Exception e) { System.err.println("[ApiClient] deleteMenuItem: " + e.getMessage()); }
    }

    private String toMenuItemJson(MenuItem item) throws Exception {
        ObjectNode n = mapper.createObjectNode();
        n.put("name",        item.getName());
        n.put("description", item.getDescription() != null ? item.getDescription() : "");
        n.put("price",       item.getPrice());
        n.put("category",    item.getCategory() != null ? item.getCategory() : "Chung");
        if (item.getImageUrl() != null) n.put("imageUrl", item.getImageUrl());
        return mapper.writeValueAsString(n);
    }

    private MenuItem mapMenuItem(JsonNode n) {
        MenuItem m = new MenuItem(
                n.path("id").asText("0"),
                n.path("name").asText(""),
                n.path("category").asText("Chung"),
                n.path("price").asDouble(0),
                n.path("description").asText("")
        );
        m.setImageUrl(n.path("imageUrl").asText(null));
        return m;
    }

    // ═══════════════════════════════════════════════════════
    //  TABLES
    // ═══════════════════════════════════════════════════════

    public List<TableItem> getTables() {
        List<TableItem> result = new ArrayList<>();
        try {
            JsonNode arr = data(get("/api/v1/tables"));
            if (!arr.isArray()) return result;
            for (JsonNode n : arr) result.add(mapTable(n));
        } catch (Exception e) { System.err.println("[ApiClient] getTables: " + e.getMessage()); }
        return result;
    }

    public TableItem createTable(TableItem t) {
        try {
            String json = post("/api/v1/tables", toTableJson(t));
            return mapTable(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] createTable: " + e.getMessage()); return t; }
    }

    public TableItem updateTable(TableItem t) {
        try {
            String json = put("/api/v1/tables/" + t.getId(), toTableJson(t));
            return mapTable(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] updateTable: " + e.getMessage()); return t; }
    }

    public void deleteTable(String id) {
        try { delete("/api/v1/tables/" + id); }
        catch (Exception e) { System.err.println("[ApiClient] deleteTable: " + e.getMessage()); }
    }

    private String toTableJson(TableItem t) throws Exception {
        ObjectNode n = mapper.createObjectNode();
        n.put("tableNumber", t.getName());
        n.put("capacity",    t.getCapacity());
        n.put("status",      tableStatusToBe(t.getStatus()));
        return mapper.writeValueAsString(n);
    }

    private TableItem mapTable(JsonNode n) {
        return new TableItem(
                n.path("id").asText("0"),
                n.path("tableNumber").asText(""),
                n.path("capacity").asInt(4),
                beStatusToTable(n.path("status").asText("AVAILABLE"))
        );
    }

    private String tableStatusToBe(TableItem.Status s) {
        if (s == null) return "AVAILABLE";
        switch (s) {
            case BAN:       return "OCCUPIED";
            case DAT_TRUOC: return "RESERVED";
            default:        return "AVAILABLE";
        }
    }

    private TableItem.Status beStatusToTable(String s) {
        switch (s) {
            case "OCCUPIED":        return TableItem.Status.BAN;
            case "RESERVED":        return TableItem.Status.DAT_TRUOC;
            // DIRTY, CLEANING, OUT_OF_SERVICE -> hiển thị là rảnh (chưa có trạng thái FE tương ứng)
            default:                return TableItem.Status.RANH;
        }
    }

    // ═══════════════════════════════════════════════════════
    //  ORDERS
    // ═══════════════════════════════════════════════════════

    public List<Order> getOrders() {
        List<Order> result = new ArrayList<>();
        try {
            JsonNode arr = data(get("/api/v1/orders"));
            if (!arr.isArray()) return result;
            for (JsonNode n : arr) result.add(mapOrder(n));
        } catch (Exception e) { System.err.println("[ApiClient] getOrders: " + e.getMessage()); }
        return result;
    }

    public Order createOrder(Order o) {
        try {
            String json = post("/api/v1/orders", toOrderJson(o));
            return mapOrder(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] createOrder: " + e.getMessage()); return o; }
    }

    public Order updateOrder(Order o) {
        try {
            String json = put("/api/v1/orders/" + o.getId(), toOrderStatusJson(o));
            return mapOrder(data(json));
        } catch (Exception e) { System.err.println("[ApiClient] updateOrder: " + e.getMessage()); return o; }
    }

    public void deleteOrder(String id) {
        try { delete("/api/v1/orders/" + id); }
        catch (Exception e) { System.err.println("[ApiClient] deleteOrder: " + e.getMessage()); }
    }

    private String toOrderJson(Order o) throws Exception {
        ObjectNode n = mapper.createObjectNode();
        // tableId là String ở FE (vd "02"), cần parse sang Long
        try { n.put("tableId", Long.parseLong(o.getTableId())); }
        catch (NumberFormatException ex) { n.put("tableId", 1L); }
        n.put("status", orderStatusToBe(o.getStatus()));
        ArrayNode items = mapper.createArrayNode();
        for (Order.OrderItem item : o.getItems()) {
            ObjectNode oi = mapper.createObjectNode();
            oi.put("quantity", item.getQuantity());
            // menuItemId từ model, fallback 0 nếu chưa có
            long menuItemId = 0L;
            if (item.getMenuItemId() != null) {
                try { menuItemId = Long.parseLong(item.getMenuItemId()); } catch (NumberFormatException ignored) {}
            }
            oi.put("menuItemId", menuItemId);
            items.add(oi);
        }
        n.set("items", items);
        return mapper.writeValueAsString(n);
    }

    private String toOrderStatusJson(Order o) throws Exception {
        ObjectNode n = mapper.createObjectNode();
        n.put("status", orderStatusToBe(o.getStatus()));
        return mapper.writeValueAsString(n);
    }

    private Order mapOrder(JsonNode n) {
        String createdAt = n.path("createdAt").asText("");
        // Rút gọn ISO datetime → hiển thị đẹp hơn
        if (createdAt.length() > 16) createdAt = createdAt.substring(0, 16).replace("T", " ");

        Order o = new Order(
                n.path("id").asText("0"),
                n.path("tableId").asText("0"),
                n.path("tableNumber").asText(""),
                n.path("totalAmount").asDouble(0),
                beStatusToOrder(n.path("status").asText("IN_PROGRESS")),
                createdAt
        );

        JsonNode itemsNode = n.path("items");
        if (itemsNode.isArray()) {
            for (JsonNode i : itemsNode) {
                Order.OrderItem oi = new Order.OrderItem(
                        i.path("menuItemId").asText("0"),
                        i.path("menuItemName").asText(""),
                        i.path("quantity").asInt(0),
                        i.path("price").asDouble(0)
                );
                o.getItems().add(oi);
            }
        }
        return o;
    }

    private String orderStatusToBe(Order.Status s) {
        if (s == null) return "PENDING";
        switch (s) {
            case HOAN_THANH:   return "COMPLETED";
            case DA_HUY:       return "CANCELLED";
            default:           return "IN_PROGRESS";
        }
    }

    private Order.Status beStatusToOrder(String s) {
        switch (s) {
            case "COMPLETED":  return Order.Status.HOAN_THANH;
            case "CANCELLED":  return Order.Status.DA_HUY;
            // PENDING, CONFIRMED, IN_PROGRESS, READY, SERVED đều là đang phục vụ
            default:           return Order.Status.DANG_PHUC_VU;
        }
    }

    // ═══════════════════════════════════════════════════════
    //  EMPLOYEES (mapped from BE User)
    // ═══════════════════════════════════════════════════════

    public List<Employee> getEmployees() {
        List<Employee> result = new ArrayList<>();
        try {
            JsonNode arr = data(get("/api/v1/users"));
            if (!arr.isArray()) return result;
            for (JsonNode n : arr) result.add(mapEmployee(n));
        } catch (Exception e) { System.err.println("[ApiClient] getEmployees: " + e.getMessage()); }
        return result;
    }

    public Employee createEmployee(Employee emp) {
        try {
            String json = post("/api/v1/users", toEmployeeJson(emp, true));
            JsonNode node = data(json);
            emp.setId(node.path("id").asText(emp.getId()));
        } catch (Exception e) { System.err.println("[ApiClient] createEmployee: " + e.getMessage()); }
        return emp;
    }

    public Employee updateEmployee(Employee emp) {
        try {
            put("/api/v1/users", toEmployeeJson(emp, false));
        } catch (Exception e) { System.err.println("[ApiClient] updateEmployee: " + e.getMessage()); }
        return emp;
    }

    public void deleteEmployee(String id) {
        try { delete("/api/v1/users/" + id); }
        catch (Exception e) { System.err.println("[ApiClient] deleteEmployee: " + e.getMessage()); }
    }

    private String toEmployeeJson(Employee emp, boolean isCreate) throws Exception {
        ObjectNode n = mapper.createObjectNode();
        if (!isCreate) n.put("id", Long.parseLong(emp.getId()));
        n.put("name",    emp.getName());
        n.put("phone",   emp.getPhone() != null   ? emp.getPhone()   : "");
        n.put("address", emp.getAddress() != null ? emp.getAddress() : "");
        // Dùng email = tên_không_dấu + timestamp + @nhahang.vn để tránh trùng
        if (isCreate) {
            String baseEmail = emp.getName() != null
                ? emp.getName().toLowerCase().replaceAll("[^a-z0-9]", "") + System.currentTimeMillis()
                : "nv" + System.currentTimeMillis();
            n.put("email",    baseEmail + "@nhahang.vn");
            n.put("password", "123456");
        }
        // Role
        ObjectNode roleNode = mapper.createObjectNode();
        roleNode.put("name", employeeRoleToBe(emp.getRole()));
        n.set("role", roleNode);
        return mapper.writeValueAsString(n);
    }

    private Employee mapEmployee(JsonNode n) {
        String roleName = n.path("role").path("name").asText("");
        return new Employee(
                n.path("id").asText("0"),
                n.path("name").asText(""),
                "",                          // cccd — BE không có
                n.path("phone").asText(""),
                n.path("address").asText(""),
                n.path("createdAt").asText("").substring(0, Math.min(10, n.path("createdAt").asText("").length())),
                beRoleToEmployee(roleName)
        );
    }

    private String employeeRoleToBe(Employee.Role r) {
        if (r == null) return "PHUC_VU";
        switch (r) {
            case DAU_BEP:  return "DAU_BEP";
            case THU_NGAN: return "THU_NGAN";
            case QUAN_LY:  return "QUAN_LY";
            default:       return "PHUC_VU";
        }
    }

    private Employee.Role beRoleToEmployee(String roleName) {
        if (roleName == null) return Employee.Role.PHUC_VU;
        switch (roleName.toUpperCase()) {
            case "DAU_BEP":  return Employee.Role.DAU_BEP;
            case "THU_NGAN": return Employee.Role.THU_NGAN;
            case "QUAN_LY":  return Employee.Role.QUAN_LY;
            default:         return Employee.Role.PHUC_VU;
        }
    }
}
