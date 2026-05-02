package agriconnect.farming.auth;

import java.util.Objects;

public final class AuthService {
    private static final AuthService INSTANCE = new AuthService();

    private final UserRepository userRepository = new UserRepository();
    private final PasswordHasher passwordHasher = new PasswordHasher();

    private AuthService() {
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public User register(String fullName, String email, String password, Role role, String location, String phone) {
        String cleanName = requireText(fullName, "Full name is required");
        String cleanEmail = User.normalizeEmail(requireText(email, "Email is required"));
        String cleanPassword = requireText(password, "Password is required");
        Objects.requireNonNull(role, "role");

        if (cleanPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("An account with that email already exists");
        }

        User user = new User(cleanName, cleanEmail, passwordHasher.hash(cleanPassword), role, location, phone);
        userRepository.save(user);
        return user;
    }

    public User authenticate(String email, String password) {
        String cleanEmail = User.normalizeEmail(requireText(email, "Email is required"));
        String cleanPassword = requireText(password, "Password is required");

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordHasher.matches(cleanPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}


