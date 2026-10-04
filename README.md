# 🛒 OrderFlow

Welcome to **OrderFlow**! A robust e-commerce order management microservice built with **Java 21**, **Spring Boot**, **MySQL**, and **MongoDB**.

OrderFlow is designed to handle high-reliability order processing, real-time inventory adjustments, state transition validations, and asynchronous audit trails with polyglot persistence.

---

## 🌟 What Makes OrderFlow Special?

Managing e-commerce orders gets tricky when dealing with stock reservation, cancellation restocking, status validation, and audit histories. OrderFlow solves these problems:

- **⚡ Automatic Inventory Reservation**: When an order is placed, item stock is decremented immediately.
- **🔄 Intelligent Order Cancellation**: Cancelling an order automatically restores reserved product quantities back to stock within a single database transaction (`@Transactional`).
- **🛡️ Strict Status Transitions**: Orders follow a valid lifecycle (`PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `SHIPPED` $\rightarrow$ `DELIVERED`). Invalid transitions are rejected with clear error messages.
- **📜 Polyglot Persistence & Audit Logging**: Core transactions and relational data are stored in **MySQL**, while immutable event audit logs are captured in **MongoDB** (`order_audits`).
- **🔍 Comprehensive SLF4J Logging**: Built-in structured logging across all services and exception handlers for observability and debugging.
- **📊 Pagination & Custom Sorting**: All listing endpoints support page size, page number, and dynamic field sorting out-of-the-box.

---

## 🏗️ Architecture & Data Model

OrderFlow uses a hybrid relational and document persistence model:

### 1. Relational Model (MySQL)
Connects Customers, Orders, Line Items, Products, and Categories:

```mermaid
erDiagram
    CUSTOMER ||--o{ ORDER : places
    ORDER ||--|{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : referenced_by
    CATEGORY ||--o{ PRODUCT : categorizes

    CUSTOMER {
        Long id PK
        String name
        String email
        String phone
    }

    ORDER {
        Long id PK
        Double amount
        OrderStatus status
        Long customer_id FK
    }

    ORDER_ITEM {
        Long id PK
        Long order_id FK
        Long product_id FK
        Integer quantity
        Double price
    }

    PRODUCT {
        Long id PK
        String name
        Double price
        Long quantity
        Long category_id FK
    }
```

### 2. Audit Document Model (MongoDB)
Captures chronological event history in the `order_audits` collection:

```json
{
  "_id": "ObjectId(...)",
  "orderId": 101,
  "action": "ORDER_CREATED",
  "timestamp": "2026-10-04T05:49:30.524Z",
  "details": "Testing MongoDB integration",
  "_class": "com.orderflow.audit.OrderAudit"
}
```

---

## 🛠️ Tech Stack

- **Java 21** & **Spring Boot**
- **Spring Data JPA** & **Hibernate** (MySQL)
- **Spring Data MongoDB** (Audit Logs)
- **MySQL 8.4** & **MongoDB Latest** (Containerized via Docker)
- **H2 Database** (In-Memory for Integration & Unit Tests)
- **SLF4J & Logback** (Structured application logging)
- **Docker & Docker Compose**
- **JUnit 5 & Mockito** (Automated tests)

---

## 🚀 Getting Started

### Prerequisites
- **JDK 21** or later installed
- **Docker Desktop** (for MySQL & MongoDB containers)
- **Maven** (bundled via `./mvnw` or installed locally)

---

### 1. Configure Environment Variables
Create a `.env` file in the `orderflow` root directory:

```env
DB_URL=jdbc:mysql://localhost:3306/orderflow
DB_USERNAME=root
DB_PASSWORD=root
```

Spring Boot connects to MongoDB via `application.properties`:
```properties
spring.mongodb.uri=mongodb://${DB_USERNAME}:${DB_PASSWORD}@localhost:27017/orderflow?authSource=admin
```

### 2. Start MySQL & MongoDB Containers
Use Docker Compose to launch both databases:

```bash
docker-compose up -d
```

### 3. Run the Application
Start the Spring Boot server:

```bash
./mvnw spring-boot:run
```
The server will start at `http://localhost:8080`.

---

## 🔌 API Quick Reference

### 📦 Orders API (`/orders`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/orders` | Place a new order (reserves inventory & sets status to `CONFIRMED`) |
| `GET` | `/orders` | List all orders with pagination & sorting (`?page=0&size=10&sort=amount,desc`) |
| `GET` | `/orders/{id}` | Get detailed order summary by Order ID |
| `POST` | `/orders/{id}/status` | Update order status (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`) |
| `POST` | `/orders/{id}/cancel` | Cancel an order & automatically return stock to product inventory |
| `GET` | `/orders/customer/{customerId}` | List all orders placed by a specific customer |
| `GET` | `/orders/status/{status}` | Filter orders by status (e.g. `/orders/status/CANCELLED`) |

### 📜 Order Audit API (`/order_audit`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/order_audit/test` | Record a test audit log event to MongoDB |

---

## 💡 Example Requests & Responses

#### Place an Order (`POST /orders`)

**Request Body:**
```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 2,
      "quantity": 2
    }
  ]
}
```

**Response (`201 Created`):**
```json
{
  "orderId": 100,
  "customer": {
    "id": 1,
    "name": "Jane Doe",
    "phone": "9876543210",
    "email": "jane@example.com"
  },
  "amount": 100.0,
  "status": "CONFIRMED",
  "items": [
    {
      "productId": 2,
      "productName": "Wireless Mouse",
      "quantity": 2,
      "price": 100.0
    }
  ]
}
```

#### Cancel an Order (`POST /orders/100/cancel`)

**Response (`200 OK`):**
```json
{
  "orderId": 100,
  "status": "CANCELLED",
  "amount": 100.0
}
```
*(Product stock for item #2 is automatically increased back by 2 in the database)*

---

## 🧪 Running Tests

To run the suite of unit and integration tests:

```bash
./mvnw test
```

---

## 💻 Database Access

### MySQL (Orders, Customers, Products)

**Via Docker:**
```bash
docker exec -it orderflow-mysql mysql -u root -proot orderflow
```

**Via Local MySQL CLI:**
```bash
mysql -u root -proot -h localhost -P 3306 orderflow
```

### MongoDB (Audit Trail & Event Logs)

**Via Docker Shell (`mongosh`):**
```bash
docker exec -it orderflow-mongo mongosh "mongodb://root:root@localhost:27017/orderflow?authSource=admin"
```

Inside the interactive shell:
```javascript
use orderflow
show collections
db.order_audits.find().pretty()
```

**Direct One-Liner Query:**
```bash
docker exec orderflow-mongo mongosh "mongodb://root:root@localhost:27017/orderflow?authSource=admin" --eval "db.order_audits.find().pretty()"
```
