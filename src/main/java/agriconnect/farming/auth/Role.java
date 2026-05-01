package agriconnect.farming.auth;

public enum Role {
    FARMER,
    CUSTOMER;

    public static Role from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        return Role.valueOf(value.trim().toUpperCase());
    }
}


