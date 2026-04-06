# Bristol Backend

**Version:** 3.0.0
**Architecture:** Hexagonal / Clean Architecture
**Stack:** Spring Boot 3.2.1 + Java 17 + PostgreSQL + Maven

E-commerce backend platform for craft beer distribution with comprehensive features including user management, product catalog, shopping cart, advanced coupon system, payment integration (Mercado Pago), and AFIP invoicing.

---

## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports & Adapters)** with a multi-module Maven structure:

```
bristol-backend/
├── bristol-domain/          # Pure domain logic (entities, value objects, ports)
├── bristol-application/     # Use cases and business orchestration
├── bristol-infrastructure/  # Adapters (JPA, security, external services)
└── bristol-api/            # REST controllers and Spring Boot app
```

### Module Dependencies

```
bristol-api
    ├── bristol-infrastructure
    │       ├── bristol-application
    │       │       └── bristol-domain
    │       └── bristol-domain
    └── bristol-domain
```

---

## 🚀 Features

### ✅ Implemented in Schema v3.0.0:

1. **User Management**
   - Authentication with password hashing
   - Multiple addresses per user
   - User roles (admin/user)

2. **Product Catalog**
   - Products with variants (sizes, colors)
   - Product images (normalized)
   - Product reviews and ratings
   - Price history tracking (automatic)
   - Stock management with automatic validation

3. **Shopping Experience**
   - Persistent shopping cart
   - Product reviews system
   - Advanced product filtering

4. **Coupon System**
   - Multiple discount types (product/order/shipping)
   - Automatic and code-based coupons
   - Customer-specific coupons
   - Combinability rules
   - Usage limits tracking

5. **Order Management**
   - Complete shipping address support
   - Multiple payment attempts (retry support)
   - Order tracking and status management
   - Automatic stock validation

6. **Delivery System**
   - Delivery zones management
   - Delivery calendar
   - Scheduled deliveries

7. **Integrations**
   - Mercado Pago payment gateway
   - AFIP electronic invoicing

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2.1**
  - Spring Data JPA
  - Spring Security
  - Spring Web
  - Spring Actuator
- **PostgreSQL 16+**
- **Flyway** (database migrations)
- **JWT** (authentication)
- **Lombok** (boilerplate reduction)
- **MapStruct** (object mapping)
- **Maven** (build tool)

---

## 📦 Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **PostgreSQL 16+**
- **Git**

---

## 🏃 Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd bristol-core/bristol-backend
```

### 2. Setup PostgreSQL database

```bash
# Create database
createdb bristol

# Or using psql
psql -U postgres
CREATE DATABASE bristol;
```

### 3. Configure environment variables

Create a `.env` file or set environment variables:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=bristol
export DB_USER=postgres
export DB_PASSWORD=your_password

# JWT
export JWT_SECRET=your-super-secret-jwt-key-change-in-production

# Mercado Pago (optional for dev)
export MP_ACCESS_TOKEN=your-mp-token
export MP_PUBLIC_KEY=your-mp-public-key

# AFIP (optional for dev)
export AFIP_CUIT=20123456789
```

### 4. Build the project

```bash
mvn clean install
```

### 5. Run the application

```bash
cd bristol-api
mvn spring-boot:run
```

Or with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 6. Verify it's running

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "bristol-backend",
  "version": "3.0.0",
  "timestamp": "2025-12-11T..."
}
```

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific module
cd bristol-domain
mvn test

# Run with coverage
mvn test jacoco:report
```

---

## 📊 Database Migrations

This project uses **Flyway** for database version control.

Migrations are located in: `bristol-infrastructure/src/main/resources/db/migration/`

- **V1__initial_schema.sql** - Initial database schema (v3.0.0)

Important:
- applied migrations must be treated as immutable
- any new schema/data change must go in a new migration (`V6__...`, `V7__...`, etc.)
- local bootstrap and smoke flow are documented in `LOCAL_DEV_WORKFLOW.md`

Flyway will automatically run migrations on application startup.

### Manual migration commands:

```bash
mvn flyway:info      # Show migration status
mvn flyway:migrate   # Run pending migrations
mvn flyway:clean     # Clean database (dev only!)
```

---

## 🔧 Configuration

### Application Profiles

- **default** - Base configuration
- **dev** - Development (verbose logging, SQL logging)
- **prod** - Production (minimal logging, optimized)

### Key Configuration Files

- `bristol-api/src/main/resources/application.yml` - Main configuration
- `bristol-api/src/main/resources/application-dev.yml` - Dev overrides
- `bristol-api/src/main/resources/application-prod.yml` - Prod overrides

---

## 📡 API Endpoints

### Health & Monitoring

- `GET /api/health` - Health check
- `GET /actuator/health` - Detailed health
- `GET /actuator/metrics` - Metrics
- `GET /actuator/prometheus` - Prometheus metrics

### User Management (TODO)

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/users/me` - Get profile
- `GET /api/users/me/addresses` - List addresses

### Products (TODO)

- `GET /api/products` - List products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/{id}/reviews` - Get product reviews
- `POST /api/products/{id}/reviews` - Create review

### Shopping Cart (TODO)

- `GET /api/cart` - Get cart
- `POST /api/cart/items` - Add item
- `PUT /api/cart/items/{id}` - Update quantity
- `DELETE /api/cart/items/{id}` - Remove item

### Orders (TODO)

- `POST /api/orders` - Create order
- `GET /api/orders` - List orders
- `GET /api/orders/{id}` - Get order details

---

## 🏗️ Project Structure

```
bristol-backend/
│
├── bristol-domain/               # Domain Layer (Pure Business Logic)
│   └── src/main/java/com/bristol/domain/
│       ├── shared/               # Shared domain concepts
│       │   ├── time/            # TimeProvider abstraction
│       │   ├── exception/       # Domain exceptions
│       │   └── valueobject/     # Base value objects
│       ├── user/                # User aggregate
│       ├── product/             # Product aggregate
│       ├── order/               # Order aggregate
│       ├── coupon/              # Coupon aggregate
│       └── delivery/            # Delivery aggregate
│
├── bristol-application/          # Application Layer (Use Cases)
│   └── src/main/java/com/bristol/application/
│       ├── common/              # Common use case interfaces
│       ├── user/                # User use cases
│       ├── product/             # Product use cases
│       ├── order/               # Order use cases
│       ├── coupon/              # Coupon use cases
│       └── auth/                # Authentication use cases
│
├── bristol-infrastructure/       # Infrastructure Layer (Adapters)
│   └── src/main/java/com/bristol/infrastructure/
│       ├── persistence/
│       │   ├── entity/         # JPA entities
│       │   ├── repository/     # JPA repositories + implementations
│       │   └── mapper/         # Domain ↔ Entity mappers
│       ├── security/            # JWT, Spring Security config
│       ├── time/                # SystemTimeProvider implementation
│       └── config/              # Infrastructure configs
│
└── bristol-api/                  # API Layer (REST Controllers)
    └── src/main/java/com/bristol/api/
        ├── controller/          # REST controllers
        ├── dto/                 # Request/Response DTOs
        ├── config/              # API configs (CORS, Swagger)
        ├── exception/           # Global exception handler
        └── BristolApplication.java  # Spring Boot main class
```

---

## 🧑‍💻 Development Guidelines

### Domain-Driven Design Principles

1. **Domain Layer** is pure Java - no framework dependencies
2. **Repositories** are interfaces (ports) defined in domain
3. **Use Cases** orchestrate domain logic without business rules
4. **Infrastructure** implements ports and handles technical concerns
5. **API Layer** translates HTTP to use case calls

### Code Style

- Use **Lombok** for boilerplate (@Getter, @Builder, etc.)
- Use **MapStruct** for mapping
- Follow **SOLID** principles
- Write **unit tests** for domain logic
- Write **integration tests** for API endpoints

---

## 📝 Development Phases

This project is being developed in phases according to `backend-development-plan.md.txt`:

- [x] **FASE 1** - Project setup and basic architecture
- [ ] **FASE 2** - Domain base (users, distributors, products, delivery zones)
- [ ] **FASE 3** - Authentication (JWT) and security
- [ ] **FASE 4** - Coupons and discount logic
- [ ] **FASE 5** - Cart, orders, payments (Mercado Pago), deliveries
- [ ] **FASE 6** - AFIP, metrics, hardening
- [ ] **FASE 7** - Final documentation

---

## 🐳 Docker Support (TODO)

```bash
# Build image
docker build -t bristol-backend:3.0.0 .

# Run with docker-compose
docker-compose up -d
```

---

## 📄 License

Copyright © 2025 Bristol Platform

---

## 👥 Team

- **3 Backend Developers** working in parallel on different modules

---

## 📚 Additional Documentation

- [Backend Development Plan](../backend-development-plan.md.txt)
- [Database Schema](../schema.sql)
- API Documentation (TODO: Swagger/OpenAPI)

---

## 🤝 Contributing

1. Create a feature branch (`git checkout -b feature/amazing-feature`)
2. Commit your changes (`git commit -m 'Add amazing feature'`)
3. Push to the branch (`git push origin feature/amazing-feature`)
4. Open a Pull Request

---

## 📞 Support

For questions or issues, please open an issue on the repository or contact the team.

---

**Happy Coding! 🍺**
