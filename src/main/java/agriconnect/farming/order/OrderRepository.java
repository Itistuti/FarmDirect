package agriconnect.farming.order;

import agriconnect.farming.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OrderRepository {
    private static OrderRepository instance;

    private OrderRepository() {
        initSchema();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    private void initSchema() {
        String ddl = "CREATE TABLE IF NOT EXISTS orders (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "product_id BIGINT NOT NULL, " +
                "farmer_id VARCHAR(36) NOT NULL, " +
                "customer_id VARCHAR(36) NOT NULL, " +
                "customer_name VARCHAR(200) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "location VARCHAR(255) NOT NULL, " +
                "phone_number VARCHAR(50) NOT NULL, " +
                "created_at_epoch_ms BIGINT NOT NULL" +
                ")";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(ddl);
            try {
                statement.execute("ALTER TABLE orders ADD COLUMN status VARCHAR(50) DEFAULT 'ACTIVE'");
            } catch (Exception ignored) {
                // Column might already exist
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize order database schema", ex);
        }
    }

    public Order save(Order order) {
        String sql = "INSERT INTO orders (id, product_id, farmer_id, customer_id, customer_name, quantity, location, phone_number, created_at_epoch_ms, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "product_id = VALUES(product_id), " +
                "farmer_id = VALUES(farmer_id), " +
                "customer_id = VALUES(customer_id), " +
                "customer_name = VALUES(customer_name), " +
                "quantity = VALUES(quantity), " +
                "location = VALUES(location), " +
                "phone_number = VALUES(phone_number), " +
                "created_at_epoch_ms = VALUES(created_at_epoch_ms), " +
                "status = VALUES(status)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, order.getId().toString());
            statement.setLong(2, order.getProductId());
            statement.setString(3, order.getFarmerId().toString());
            statement.setString(4, order.getCustomerId().toString());
            statement.setString(5, order.getCustomerName());
            statement.setInt(6, order.getQuantity());
            statement.setString(7, order.getLocation());
            statement.setString(8, order.getPhoneNumber());
            statement.setLong(9, order.getCreatedAt().toEpochMilli());
            statement.setString(10, order.getStatus());
            statement.executeUpdate();
            return order;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to save order to database", ex);
        }
    }

    public List<Order> findByFarmerId(UUID farmerId) {
        String sql = "SELECT * FROM orders WHERE farmer_id = ? ORDER BY created_at_epoch_ms DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, farmerId.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
            return orders;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to find orders by farmerId", ex);
        }
    }

    public List<Order> findByCustomerId(UUID customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at_epoch_ms DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
            return orders;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to find orders by customerId", ex);
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY created_at_epoch_ms DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
            return orders;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to list orders from database", ex);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        return new Order(
                UUID.fromString(rs.getString("id")),
                rs.getLong("product_id"),
                UUID.fromString(rs.getString("farmer_id")),
                UUID.fromString(rs.getString("customer_id")),
                rs.getString("customer_name"),
                rs.getInt("quantity"),
                rs.getString("location"),
                rs.getString("phone_number"),
                Instant.ofEpochMilli(rs.getLong("created_at_epoch_ms")),
                rs.getString("status")
        );
    }
}
