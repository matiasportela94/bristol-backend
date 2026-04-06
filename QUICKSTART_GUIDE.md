# Bristol Backend - Quick Start Guide

## Prerequisites Checklist

Before running the application, ensure you have:

- [ ] **Java 17** installed (`java -version`)
- [ ] **Maven** installed (`mvn -version`)
- [ ] **PostgreSQL** running on `localhost:5432`
- [ ] **Database** named `bristol` created
- [ ] **IntelliJ IDEA** installed

---

## Step 1: Database Setup

### Start PostgreSQL
Make sure PostgreSQL is running on your system.

### Create Database
```sql
CREATE DATABASE bristol;
```

### Verify Connection
Default credentials (configurable in run configuration):
- **Host:** localhost
- **Port:** 5432
- **Database:** bristol
- **Username:** admin
- **Password:** secret

---

## Step 2: IntelliJ Run Configuration

### Option A: Use the Pre-configured Run Configuration

1. The run configuration file has been created at:
   `.idea/runConfigurations/Bristol_Backend.xml`

2. Restart IntelliJ or reload the project

3. You should see "Bristol Backend" in your run configurations dropdown

4. Click the green "Run" button

### Option B: Create Manually

1. Go to `Run` → `Edit Configurations...`
2. Click `+` → `Spring Boot`
3. Configure:
   - **Name:** Bristol Backend
   - **Main class:** `com.bristol.api.BristolApplication`
   - **Module:** `bristol-api`
   - **Working directory:** `C:\dev\CITRINO\git\bristol-core\bristol-backend`
4. Environment variables (optional):
   ```
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=bristol
   DB_USER=admin
   DB_PASSWORD=secret
   SERVER_PORT=8080
   LOG_LEVEL=DEBUG
   ```
5. Click `Apply` → `OK`

---

## Step 3: Run the Application

### In IntelliJ:
1. Select "Bristol Backend" from the run configurations dropdown
2. Click the green "Run" button (or press Shift+F10)
3. Wait for the application to start

### Look for this in the console:
```
Started BristolApplication in X.XXX seconds
```

### Verify it's running:
Open your browser and go to:
- **Health Check:** http://localhost:8080/actuator/health
- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Step 4: Import Postman Collection

### Import the Collection:

1. Open **Postman**
2. Click **Import** (top left)
3. Select **File**
4. Navigate to:
   `C:\dev\CITRINO\git\bristol-core\bristol-backend\Bristol-Backend.postman_collection.json`
5. Click **Import**

You should now see "Bristol Backend API" in your collections.

---

## Step 5: Test the API with Postman

### Test Flow:

#### 1. **Login as Admin**
- Folder: `Authentication`
- Request: `Login Admin`
- Click **Send**
- The token will be automatically saved to the collection variables

**Credentials:**
```json
{
    "email": "admin@bristol.com",
    "password": "Admin123!"
}
```

#### 2. **Create a Product**
- Folder: `Products`
- Request: `Create Product (ADMIN)`
- Click **Send**
- The product ID will be automatically saved

#### 3. **Get All Products**
- Folder: `Products`
- Request: `Get All Products`
- Click **Send**
- You should see your created product

#### 4. **Create a Coupon**
- Folder: `Coupons`
- Request: `Create Coupon (ADMIN)`
- Click **Send**

#### 5. **Create an Order**
- Folder: `Orders`
- Request: `Create Order`
- **Important:** Update the request body with valid IDs
- Click **Send**

#### 6. **Update Order Status to PAID**
- Folder: `Orders`
- Request: `Update Order Status (ADMIN)`
- Click **Send**
- This will automatically deduct stock

---

## Postman Collection Features

### Automatic Token Management
The collection automatically:
- Saves the JWT token when you login
- Includes the token in all authenticated requests
- Saves userId, productId, and orderId for reuse

### Collection Variables
You can view/edit these in Postman:
- `{{baseUrl}}` - http://localhost:8080
- `{{token}}` - Auto-populated after login
- `{{userId}}` - Auto-populated after login/register
- `{{productId}}` - Auto-populated after creating a product
- `{{orderId}}` - Auto-populated after creating an order

---

## Common Issues & Solutions

### Issue: "Failed to connect to database"
**Solution:**
- Verify PostgreSQL is running
- Check database credentials in `application.yml`
- Ensure database `bristol` exists

### Issue: "Port 8080 already in use"
**Solution:**
- Change port in environment variables: `SERVER_PORT=8081`
- Or stop the process using port 8080

### Issue: "Flyway migration failed"
**Solution:**
- Check the current migration policy in `LOCAL_DEV_WORKFLOW.md`.
- For new schema changes, add a new migration instead of editing an applied one.
- Only use `repair` intentionally in local/dev databases after validating the migration directory.

### Issue: "401 Unauthorized" in Postman
**Solution:**
- Run "Login Admin" request first
- Verify token is saved in collection variables
- Check that Authorization header is set to `Bearer {{token}}`

---

## API Endpoints Summary

### Public Endpoints (No Auth Required):
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/featured` - Get featured products
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/coupons/active` - Get active coupons
- `POST /api/coupons/validate` - Validate coupon code
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Metrics

### User Endpoints (USER or ADMIN):
- All order operations for their own orders
- User profile management
- User address management

### Admin Endpoints (ADMIN only):
- Product CRUD
- Order status management
- Coupon CRUD
- Distributor CRUD
- Delivery zone management
- Assign orders to distributors

---

## Development Workflow

### Typical Test Flow:

1. **Start Application** in IntelliJ
2. **Login** as admin in Postman
3. **Create Products** (beers)
4. **Create Delivery Zone**
5. **Create User Address** for the admin user
6. **Create Coupon** (optional)
7. **Create Order** with products
8. **Apply Coupon** to order (optional)
9. **Update Order Status** to PAID (deducts stock)
10. **Create Distributor**
11. **Assign Distributor** to order
12. **Update Order Status** to PROCESSING → SHIPPED → DELIVERED

---

## Next Steps

- Explore all endpoints in the Postman collection
- Check Swagger UI for API documentation: http://localhost:8080/swagger-ui.html
- Review the API responses and data models
- Test error scenarios (invalid data, unauthorized access, etc.)

---

## Useful Links

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs
- **Health Check:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics

---

## Support

If you encounter issues:
1. Check the IntelliJ console for error messages
2. Verify PostgreSQL is running and accessible
3. Review the `CLAUDE.md` documentation
4. Check application logs for detailed error information

---

**Happy Testing!** 🍺
