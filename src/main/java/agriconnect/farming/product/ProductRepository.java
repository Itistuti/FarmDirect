package agriconnect.farming.product;

import agriconnect.farming.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepository {
    private static ProductRepository instance;

    private ProductRepository() {
        initSchema();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    private void initSchema() {
        String ddl = "CREATE TABLE IF NOT EXISTS products (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "farmer_id VARCHAR(36) NOT NULL, " +
                "name VARCHAR(200) NOT NULL, " +
                "category VARCHAR(100) NOT NULL, " +
                "description TEXT NOT NULL, " +
                "price DOUBLE NOT NULL, " +
                "stock INT NOT NULL, " +
                "created_at_epoch_ms BIGINT NOT NULL, " +
                "updated_at_epoch_ms BIGINT NOT NULL" +
                ")";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize product database schema", ex);
        }
    }

    public Product save(Product product) {
        if (product.getId() <= 0) {
            String sql = "INSERT INTO products (farmer_id, name, category, description, price, stock, created_at_epoch_ms, updated_at_epoch_ms) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                statement.setString(1, product.getFarmerId().toString());
                statement.setString(2, product.getName());
                statement.setString(3, product.getCategory());
                statement.setString(4, product.getDescription());
                statement.setDouble(5, product.getPrice());
                statement.setInt(6, product.getStock());
                statement.setLong(7, product.getCreatedAt().toEpochMilli());
                statement.setLong(8, product.getUpdatedAt().toEpochMilli());
                
                statement.executeUpdate();
                
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        return new Product(id, product.getFarmerId(), product.getName(), product.getCategory(),
                                product.getDescription(), product.getPrice(), product.getStock(),
                                product.getCreatedAt(), product.getUpdatedAt());
                    } else {
                        throw new SQLException("Creating product failed, no ID obtained.");
                    }
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to save product to database", ex);
            }
        } else {
            String sql = "UPDATE products SET farmer_id = ?, name = ?, category = ?, description = ?, price = ?, stock = ?, updated_at_epoch_ms = ? WHERE id = ?";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                
                statement.setString(1, product.getFarmerId().toString());
                statement.setString(2, product.getName());
                statement.setString(3, product.getCategory());
                statement.setString(4, product.getDescription());
                statement.setDouble(5, product.getPrice());
                statement.setInt(6, product.getStock());
                statement.setLong(7, product.getUpdatedAt().toEpochMilli());
                statement.setLong(8, product.getId());
                
                statement.executeUpdate();
                return product;
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to update product in database", ex);
            }
        }
    }

    public Optional<Product> findById(long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to find product by id", ex);
        }
    }

    public List<Product> findByFarmerId(UUID farmerId) {
        String sql = "SELECT * FROM products WHERE farmer_id = ? ORDER BY updated_at_epoch_ms DESC";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, farmerId.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to find products by farmerId", ex);
        }
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY updated_at_epoch_ms DESC";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to list products from database", ex);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to delete product from database", ex);
        }
    }

    public List<Product> findByCategory(String category) {
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY updated_at_epoch_ms DESC";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to find products by category", ex);
        }
    }

    public List<Product> searchByName(String query) {
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ? ORDER BY updated_at_epoch_ms DESC";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + query.toLowerCase() + "%");
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to search products by name", ex);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        UUID farmerId = UUID.fromString(rs.getString("farmer_id"));
        String name = rs.getString("name");
        String category = rs.getString("category");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        int stock = rs.getInt("stock");
        long createdAtEpochMs = rs.getLong("created_at_epoch_ms");
        long updatedAtEpochMs = rs.getLong("updated_at_epoch_ms");
        
        return new Product(id, farmerId, name, category, description, price, stock, 
                Instant.ofEpochMilli(createdAtEpochMs), Instant.ofEpochMilli(updatedAtEpochMs));
    }
}

