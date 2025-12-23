# 🛒 CardShop Backend

A Spring Boot backend application for managing products, carts, and orders with support for role-based access, caching, and concurrency-safe stock handling.

---

## ✨ Features

- User authentication & authorization (JWT, roles: ADMIN / SELLER / CUSTOMER)
- Product management with categories
- Cart & checkout flow
- Order creation with stock consistency
- Seller order views & filtering
- Redis caching for read-heavy endpoints
- Pagination, sorting, and filtering
- RESTful API design

---

## 🏗 Architecture Overview

- **Controller layer**: Handles HTTP requests and responses
- **Service layer**: Business logic and transactions
- **Repository layer**: JPA / Specifications
- **Security**: Spring Security + JWT
- **Caching**: Redis with Spring Cache abstraction
- **Database**: JPA/Hibernate (H2 / MySQL)

---

## 🛠 Tech Stack

- Java 21+
- Spring Boot
- Spring Data JPA
- Spring Security (JWT)
- Redis
- Hibernate
- Docker & Docker Compose
- JUnit / Mockito

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Docker
- Maven

### Run with Docker
```bash
  docker compose up -d
```

### Observability Stack
-	Zipkin: Distributed tracing UI
→ http://localhost:9411
-	Prometheus: Metrics scraping and querying
→ http://localhost:9090
-	Grafana: Metrics dashboards and visualization
→ http://localhost:3000 (Default credentials: admin / admin)

⸻
### Docker Networking Notes
-	MySQL: mysql:3306
-	Redis: redis:6379
-	OTEL Collector: otel-collector:4317
