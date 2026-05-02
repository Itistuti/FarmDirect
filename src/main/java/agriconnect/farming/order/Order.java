package agriconnect.farming.order;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final UUID id;
    private final long productId;
    private final UUID farmerId;
    private final UUID customerId;
    private final String customerName;
    private final int quantity;
    private final String location;
    private final String phoneNumber;
    private final Instant createdAt;
    private final String status;

    public Order(long productId,
                 UUID farmerId,
                 UUID customerId,
                 String customerName,
                 int quantity,
                 String location,
                 String phoneNumber) {
        this(UUID.randomUUID(), productId, farmerId, customerId, customerName, quantity, location, phoneNumber, Instant.now(), "ACTIVE");
    }

    public Order(UUID id,
                 long productId,
                 UUID farmerId,
                 UUID customerId,
                 String customerName,
                 int quantity,
                 String location,
                 String phoneNumber,
                 Instant createdAt,
                 String status) {
        this.id = Objects.requireNonNull(id, "id");
        this.productId = productId;
        this.farmerId = Objects.requireNonNull(farmerId, "farmerId");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.customerName = Objects.requireNonNull(customerName, "customerName");
        this.quantity = quantity;
        this.location = Objects.requireNonNull(location, "location");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = status != null ? status : "ACTIVE";
    }

    public UUID getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public UUID getFarmerId() {
        return farmerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public Order withStatus(String newStatus) {
        return new Order(id, productId, farmerId, customerId, customerName, quantity, location, phoneNumber, createdAt, newStatus);
    }
}
