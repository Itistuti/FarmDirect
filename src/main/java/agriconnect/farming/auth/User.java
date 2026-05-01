package agriconnect.farming.auth;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class User {
    private final UUID id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Instant createdAt;

    public User(String fullName, String email, String passwordHash, Role role) {
        this(UUID.randomUUID(), fullName, email, passwordHash, role, Instant.now());
    }

    public User(UUID id, String fullName, String email, String passwordHash, Role role, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.email = normalizeEmail(email);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User withPasswordHash(String newPasswordHash) {
        return new User(id, fullName, email, newPasswordHash, role, createdAt);
    }

    public static String normalizeEmail(String value) {
        return Objects.requireNonNull(value, "email").trim().toLowerCase();
    }
}

