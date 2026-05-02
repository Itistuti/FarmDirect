# FarmDirect - Farm-to-Customer E-Commerce Platform

A Jakarta EE-based web application that connects farmers directly with customers for fresh vegetable sales.

## Features

### Implemented
- **Authentication System**
  - Multi-role registration (Farmer / Customer)
  - Secure login with hashed passwords (PBKDF2)
  - Session-based user management
  - Role-based access control

- **Farmer Dashboard** (`/farmer-dashboard.html`)
  - Add, edit, and delete vegetable listings
  - Manage inventory stock levels
  - Product categorization
  - Real-time product statistics
  - Price management
  - Responsive design

- **Frontend Pages**
  - Login page with clean UI
  - Signup page with role selection
  - Dashboard with role-based routing
  - Farmer dashboard with full CRUD operations

### Coming soon
- Customer dashboard for browsing products
- Shopping cart functionality
- Order placement and tracking
- Order history for customers and farmers

## Technology Stack

- **Backend**: Jakarta EE, Jersey (JAX-RS), Weld (CDI)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Security**: PBKDF2 password hashing, HTTP session management
- **Build**: Apache Maven
- **Runtime**: Java 24+, Servlet 6.1+

## Project Structure

```
src/main/
├── java/agriconnect/farming/
│   ├── HelloApplication.java          # JAX-RS configuration
│   ├── HelloResource.java             # Sample endpoint
│   ├── auth/                          # Authentication & authorization
│   │   ├── AuthResource.java          # Login/signup/logout endpoints
│   │   ├── AuthService.java           # Auth business logic
│   │   ├── User.java                  # User entity
│   │   ├── Role.java                  # User roles
│   │   ├── UserRepository.java        # User storage
│   │   ├── PasswordHasher.java        # Secure password hashing
│   │   ├── SecureRole.java            # Role annotation
│   │   ├── RoleFilter.java            # Request filter for role checks
│   │   └── SessionKeys.java           # Session constant keys
│   └── product/                       # Product management
│       ├── Product.java               # Product entity
│       ├── ProductRepository.java     # Product storage
│       └── FarmerProductResource.java # Farmer product endpoints
└── webapp/
    ├── index.html                     # Root page (redirect)
    ├── login.html                     # Login page
    ├── signup.html                    # Signup page
    ├── dashboard.html                 # Main dashboard (role redirect)
    ├── farmer-dashboard.html          # Farmer product management
    └── assets/
        ├── styles.css                 # Global styles
        ├── auth.js                    # Auth-related JS functions
        └── farmer.js                  # Farmer dashboard logic
```

## Getting Started

### Prerequisites
- Java 24+ (JDK)
- Maven 3.9+

### Build & Run

```bash
# Set JAVA_HOME (if not already set)
$env:JAVA_HOME='C:\Program Files\Java\jdk-24'

# Clean build
.\mvnw.cmd clean install

# Package as WAR
.\mvnw.cmd package

# Deploy using your favorite servlet container (Tomcat, etc.)
# Or use:
java -jar target/runner.jar
```

### Access the Application

After deployment, open your browser and navigate to:
- **Root**: `http://localhost:8081/` → Redirects to login if not authenticated
- **Login**: `http://localhost:8081/login.html`
- **Signup**: `http://localhost:8081/signup.html`
- **Farmer Dashboard**: `http://localhost:8081/farmer-dashboard.html`

## API Endpoints

### Authentication (`/api/auth/`)
- `POST /signup` - Register a new user
  ```json
  {
    "fullName": "John Farmer",
    "email": "john@farm.com",
    "password": "secure123",
    "role": "FARMER"
  }
  ```

- `POST /login` - Authenticate user
  ```json
  {
    "email": "john@farm.com",
    "password": "secure123"
  }
  ```

- `POST /logout` - Clear session

- `GET /current` - Get current logged-in user info

### Farmer Products (`/api/farmer/products/`) - *Requires FARMER role*
- `POST /` - Add new product
  ```json
  {
    "name": "Organic Tomatoes",
    "category": "Fruits",
    "description": "Fresh pesticide-free tomatoes",
    "price": 5.99,
    "stock": 100
  }
  ```

- `GET /` - List all products for current farmer

- `GET /{id}` - Get product by ID

- `PUT /{id}` - Update product details
  ```json
  {
    "name": "Organic Tomatoes",
    "category": "Fruits",
    "description": "Updated description",
    "price": 6.99,
    "stock": 150
  }
  ```

- `DELETE /{id}` - Delete product

- `PUT /{id}/stock` - Update stock only
  ```json
  {
    "stock": 75
  }
  ```

## Farmer Dashboard Features

### Add Product
1. Fill in the form on the left sidebar:
   - Product name (required)
   - Category (required)
   - Description (optional)
   - Price per unit (required)
   - Stock quantity (required)
2. Click "Add Product"

### View Products
- See all your products in the main section
- Products sorted by most recently updated
- Shows product name, category, description, price, and stock level

### Edit Product
1. Click "Edit" on any product card
2. Form autofills with current values
3. Make changes
4. Click "Update Product"

### Delete Product
1. Click "Delete" on any product card
2. Confirm the deletion

### Stock Management
- Stock levels are color-coded:
  - **Green**: Normal stock (≥10 units)
  - **Yellow**: Low stock (1-9 units)
  - **Red**: Out of stock (0 units)

### Statistics
- **Total Products**: Number of products you've listed
- **In Stock**: Products with quantity > 0
- **Low Stock**: Products with 1-9 units

## Security Features

- **Password Hashing**: PBKDF2 with SHA-256
- **Session Management**: HTTP session-based authentication
- **Role-Based Access Control**: Endpoints protected by role annotations
- **Input Validation**: Server-side validation on all inputs
- **XSS Protection**: HTML escaping in JavaScript
- **CSRF Protection**: Session-based state

## Data Storage

**Note**: Currently using in-memory storage. Data persists only during the application lifecycle. For production, integrate a database (PostgreSQL, MySQL, etc.).

### Future: Database Integration
1. Add JPA/Hibernate dependency to pom.xml
2. Replace `ProductRepository` and `UserRepository` with JPA implementations
3. Create database tables for users, products, orders

## Troubleshooting

### "Not authenticated" error
- Make sure you're logged in
- Check that your session hasn't expired
- Try logging in again

### "You do not own this product" error
- You can only edit/delete your own products
- Check that you're using the correct product ID

### Port already in use
- Change the port in your application server configuration
- Default is 8081 as per recent changes

## Development Notes

- Java version: 24 (uses latest features like records support)
- Maven compiler target: 24
- Jackson for JSON serialization
- CDI for dependency injection
- Jersey as JAX-RS implementation

## Next Steps

1. **Customer Dashboard**: Create product browsing and search functionality
2. **Shopping Cart**: Implement session-based cart with add/remove operations
3. **Orders**: Add order placement and status tracking
4. **Database**: Replace in-memory storage with persistent database
5. **Payment Integration**: Add payment processing
6. **Notifications**: Email notifications for orders and stock updates

## License

Proprietary - FarmDirect Project

## Contact

For issues or questions, please contact the development team.

