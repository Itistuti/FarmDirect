package agriconnect.farming.product;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Product {
    private final long id;
    private final UUID farmerId;
    private final String name;
    private final String category;
    private final String description;
    private final double price;
    private final int stock;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Product(UUID farmerId, String name, String category, String description, double price, int stock) {
        this(0L, farmerId, name, category, description, price, stock, Instant.now(), Instant.now());
    }

    public Product(long id, UUID farmerId, String name, String category, String description, double price, int stock, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.farmerId = Objects.requireNonNull(farmerId, "farmerId");
        this.name = Objects.requireNonNull(name, "name");
        this.category = Objects.requireNonNull(category, "category");
        this.description = Objects.requireNonNull(description, "description");
        this.price = price;
        this.stock = stock;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public long getId() {
        return id;
    }

    public UUID getFarmerId() {
        return farmerId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Product withStock(int newStock) {
        return new Product(id, farmerId, name, category, description, price, newStock, createdAt, Instant.now());
    }

    public Product withDetails(String newName, String newCategory, String newDescription, double newPrice) {
        return new Product(id, farmerId, newName, newCategory, newDescription, newPrice, stock, createdAt, Instant.now());
    }
}

