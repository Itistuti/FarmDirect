# FarmDirect - Farm-to-Customer E-Commerce Platform

A Jakarta EE-based web application that connects farmers directly with customers for fresh vegetable sales.

## Features

### Implemented
- **Authentication System**
  - Multi-role registration (Farmer / Customer) with Location
  - Secure login with hashed passwords (PBKDF2)
  - Session-based user management
  - Role-based access control

- **Farmer Dashboard** (`/farmer-dashboard.jsp`)
  - Add, edit, and delete vegetable listings
  - Manage inventory stock levels
  - Manage and track customer orders
  - Product categorization
  - Real-time product and order statistics
  - Responsive design and profile management

- **Customer Dashboard** (`/customer-dashboard.jsp`)
  - Browse and search products from local farmers
  - Shopping cart functionality
  - Order placement and history
  - Direct connection to farmers and their locations

- **Frontend Pages**
  - Landing page with modern design (`/index.jsp`)
  - Information pages: About Us, Services, Contact Us
  - Login & Signup pages with role selection
  - Dynamic UI with JavaServer Pages (JSP)

## Technology Stack

- **Backend**: Jakarta EE, Servlets, Jersey (JAX-RS), Weld (CDI)
- **Frontend**: JSP, HTML5, CSS3, Vanilla JavaScript
- **Security**: PBKDF2 password hashing, HTTP session management
- **Build**: Apache Maven
- **Runtime**: Java 24+, Servlet 6.1+

## Project Structure

```
src/main/
├── java/agriconnect/farming/
│   ├── auth/                          # Authentication & authorization
│   │   ├── AuthServlet.java           # Main Servlet for login/signup
│   │   ├── AuthService.java           # Auth business logic
│   │   ├── User.java                  # User entity
│   │   ├── Role.java                  # User roles
│   │   ├── SessionKeys.java           # Session constant keys
│   │   └── ...                        
│   └── product/                       # Product & Order management
│       ├── Product.java               # Product entity
│       ├── Order.java                 # Order entity
│       └── ...
└── webapp/
    ├── index.jsp                      # Landing page
    ├── login.jsp                      # Login page
    ├── signup.jsp                     # Signup page
    ├── farmer-dashboard.jsp           # Farmer management interface
    ├── customer-dashboard.jsp         # Customer shopping interface
    ├── about.jsp                      # About us page
    ├── services.jsp                   # Services page
    ├── contact.jsp                    # Contact us page
    └── assets/
        ├── styles.css                 # Global styles
        ├── auth.js                    # Auth-related JS functions
        ├── farmer.js                  # Farmer dashboard logic
        └── customer.js                # Customer shopping logic
```

## Getting Started

### Prerequisites
- Java 24+ (JDK)
- Maven 3.9+
- A compatible Servlet container (like Tomcat)

### Build & Run

```bash
# Clean build
./mvnw clean install

# Package as WAR
./mvnw package

# Or run using Maven (if a plugin like cargo or embedded tomcat is configured)
# or deploy the generated WAR to your Application Server.
```

### Access the Application

After deployment, open your browser and navigate to the application root. Based on your session, it will intelligently redirect you:
- **Root**: `http://localhost:8080/FarmDirect` (depending on your context path)
- **Farmer Dashboard**: `http://localhost:8080/FarmDirect/farmer-dashboard.jsp`
- **Customer Dashboard**: `http://localhost:8080/FarmDirect/customer-dashboard.jsp`

## Security Features

- **Password Hashing**: PBKDF2 with SHA-256
- **Session Management**: HTTP session-based authentication
- **Role-Based Access Control**: Pages and endpoints protected by role checks
- **Input Validation**: Server-side validation on all inputs
- **XSS Protection**: HTML escaping in JavaScript and JSPs
- **CSRF Protection**: Session-based state management

## Data Storage

**Note**: Currently using in-memory storage for rapid prototyping. Data persists only during the application lifecycle. For production, integrate a database (PostgreSQL, MySQL, etc.).

### Future: Database Integration
1. Add JPA/Hibernate dependency to `pom.xml`
2. Replace repositories with JPA implementations
3. Create database tables for users, products, and orders

## License

Proprietary - FarmDirect Project

## Contact

For issues or questions, please contact the development team at `support@farmdirect.com.np`.
