package agriconnect.farming.auth;

import agriconnect.farming.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class UserRepository {
    public UserRepository() {
        initSchema();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        String normalizedEmail = User.normalizeEmail(email);
        String sql = "SELECT id, full_name, email, password_hash, role, location, phone, created_at_epoch_ms FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedEmail);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read user from database", ex);
        }
    }

    public Optional<User> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT id, full_name, email, password_hash, role, location, phone, created_at_epoch_ms FROM users WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read user from database by id", ex);
        }
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public User save(User user) {
        Objects.requireNonNull(user, "user");
        String sql = "INSERT INTO users (email, id, full_name, password_hash, role, location, phone, created_at_epoch_ms) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = VALUES(id), " +
                "full_name = VALUES(full_name), " +
                "password_hash = VALUES(password_hash), " +
                "role = VALUES(role), " +
                "location = VALUES(location), " +
                "phone = VALUES(phone), " +
                "created_at_epoch_ms = VALUES(created_at_epoch_ms)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getId().toString());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRole().name());
            statement.setString(6, user.getLocation());
            statement.setString(7, user.getPhone());
            statement.setLong(8, user.getCreatedAt().toEpochMilli());
            statement.executeUpdate();
            return user;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to save user to database", ex);
        }
    }

    public Collection<User> findAll() {
        String sql = "SELECT id, full_name, email, password_hash, role, location, phone, created_at_epoch_ms FROM users ORDER BY created_at_epoch_ms DESC";
        List<User> users = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to list users from database", ex);
        }
    }

    private void initSchema() {
        String ddl = "CREATE TABLE IF NOT EXISTS users (" +
                "email VARCHAR(320) PRIMARY KEY, " +
                "id VARCHAR(36) NOT NULL, " +
                "full_name VARCHAR(200) NOT NULL, " +
                "password_hash VARCHAR(200) NOT NULL, " +
                "role VARCHAR(50) NOT NULL, " +
                "created_at_epoch_ms BIGINT NOT NULL" +
                ")";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(ddl);
            try {
                statement.execute("ALTER TABLE users ADD COLUMN location VARCHAR(255)");
            } catch (Exception ignored) {
                // Column might already exist
            }
            try {
                statement.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(50)");
            } catch (Exception ignored) {
                // Column might already exist
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize user database schema", ex);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        Role role = Role.from(rs.getString("role"));
        String location = rs.getString("location");
        String phone = rs.getString("phone");
        long createdAtEpochMs = rs.getLong("created_at_epoch_ms");
        return new User(id, fullName, email, passwordHash, role, location, phone, Instant.ofEpochMilli(createdAtEpochMs));
    }
}

