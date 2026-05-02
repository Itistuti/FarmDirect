package agriconnect.farming.order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class OrderRepository {
    private static OrderRepository instance;

    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    private OrderRepository() {
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    public List<Order> findByFarmerId(UUID farmerId) {
        return orders.values().stream()
                .filter(o -> o.getFarmerId().equals(farmerId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> findByCustomerId(UUID customerId) {
        return orders.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
