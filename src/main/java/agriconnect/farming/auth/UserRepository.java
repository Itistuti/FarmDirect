package agriconnect.farming.auth;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class UserRepository {
    private final ConcurrentMap<String, User> usersByEmail = new ConcurrentHashMap<>();

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByEmail.get(User.normalizeEmail(email)));
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public User save(User user) {
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    public Collection<User> findAll() {
        return usersByEmail.values();
    }
}

