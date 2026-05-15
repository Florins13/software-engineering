# Bike Shop — Monolith to Microservices Decomposition

> **Context:** This document captures the full architecture, design decisions, and implementation details
> for decomposing the monolithic Quarkus bike shop API into 3 independent microservices.
> This work supports a thesis on **microfrontends** — the frontend was already split into
> bike, cart, and orders microfrontends. The backend decomposition creates full **vertical slices**
> of each domain.

---

## Table of Contents

1. [Architecture Decisions](#architecture-decisions)
2. [Target Architecture](#target-architecture)
3. [Service Specifications](#service-specifications)
4. [Checkout Saga Flow](#checkout-saga-flow)
5. [Semantic Lock Pattern](#semantic-lock-pattern)
6. [Kafka Topics](#kafka-topics)
7. [SSE (Server-Sent Events)](#sse-server-sent-events)
8. [Docker Compose (Local Dev)](#docker-compose-local-dev)
9. [AWS EKS Deployment Notes](#aws-eks-deployment-notes)
10. [Technical Notes & Gotchas](#technical-notes--gotchas)
11. [Files Created](#files-created)
12. [Implementation Status](#implementation-status)
13. [Design Decisions Log](#design-decisions-log)

---

## Architecture Decisions

| Decision | Choice | Rationale |
|---|---|---|
| Subdomains | Bike (product), Cart, Order | Align with microfrontend boundaries |
| User identity | `X-User-Id` header | Keep it simple — no separate user service |
| Project structure | Separate directories, same repo | Independent `pom.xml` each, shared git history |
| Inter-service comm. | Event-driven with Apache Kafka | Loose coupling, async processing |
| Cart storage | Redis | Ephemeral data, fast reads, no JPA needed |
| Stock reservation | Saga + Semantic Locks + Pessimistic Lock | Prevents dirty reads during checkout |
| Order status updates | SSE (Server-Sent Events) | Progressive rendering in the frontend |
| Java version | 17 | Quarkus 3.19.1 compatibility |
| Framework | Quarkus 3.19.1 | Matches existing monolith |

---

## Target Architecture

### High-Level Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Microfrontends)                   │
│  ┌─────────────┐    ┌─────────────┐    ┌──────────────┐            │
│  │  Bike MFE   │    │  Cart MFE   │    │  Order MFE   │            │
│  └──────┬──────┘    └──────┬──────┘    └──────┬───────┘            │
└─────────┼──────────────────┼──────────────────┼────────────────────┘
          │ :8081            │ :8082             │ :8083
          ▼                  ▼                   ▼
   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
   │ Bike Service │   │ Cart Service │   │ Order Service│
   │  (Catalog +  │   │  (Redis)     │   │  (Saga +     │
   │   Stock)     │   │              │   │   SSE)       │
   └──────┬───────┘   └──────┬───────┘   └──┬───────┬──┘
          │                  │               │       │
          ▼                  ▼               │       ▼
   ┌──────────────┐   ┌──────────────┐      │  ┌──────────────┐
   │ PostgreSQL   │   │    Redis     │      │  │ PostgreSQL   │
   │  (bike_db)   │   │              │      │  │  (order_db)  │
   │  :5433       │   │  :6379       │      │  │  :5434       │
   └──────────────┘   └──────────────┘      │  └──────────────┘
                                            │
                      ┌─────────────────────┤
                      ▼                     ▼
               ┌──────────────┐     ┌──────────────┐
               │    Kafka     │     │  REST Client  │
               │   (KRaft)    │     │  → Cart Svc   │
               │   :9092      │     │  (fetch cart)  │
               └──────────────┘     └──────────────┘
```

### Repository Structure

```
software-engineering/
├── api/                          ← existing monolith (kept as reference)
├── bike-service/                 ← Product catalog + stock management
│   ├── pom.xml
│   └── src/main/java/com/dev/bike/
│       ├── controller/
│       │   ├── BikeController.java
│       │   └── dto/BikeDTO.java
│       ├── service/BikeService.java
│       ├── repository/BikeRepository.java
│       ├── model/
│       │   ├── Product.java          (availableStock + reservedStock)
│       │   └── Bike.java
│       ├── event/StockReservationConsumer.java
│       └── Startup.java              (seed data)
├── cart-service/                  ← Cart management (Redis)
│   ├── pom.xml
│   └── src/main/java/com/dev/cart/
│       ├── controller/
│       │   ├── CartController.java
│       │   └── dto/
│       │       ├── CartDTO.java
│       │       ├── CheckoutDTO.java
│       │       └── AddToCartRequest.java
│       ├── service/CartService.java
│       ├── model/
│       │   ├── Cart.java
│       │   └── CartItem.java
│       └── event/CartEventConsumer.java
├── order-service/                 ← Order orchestration + saga
│   ├── pom.xml
│   └── src/main/java/com/dev/order/
│       ├── controller/
│       │   ├── OrderController.java
│       │   ├── OrderSseController.java
│       │   └── dto/
│       │       ├── OrderDTO.java
│       │       └── OrderRequestDTO.java
│       ├── service/
│       │   ├── OrderService.java
│       │   ├── OrderSagaOrchestrator.java
│       │   ├── OrderSseManager.java
│       │   ├── OrderStatusEvent.java
│       │   └── CartRestClient.java
│       ├── repository/OrderRepository.java
│       ├── model/
│       │   ├── Order.java
│       │   ├── ShippingItem.java
│       │   ├── ShippingAddress.java
│       │   ├── OrderState.java
│       │   └── AcquireType.java
│       └── event/OrderEventConsumer.java
├── docker-compose.yml             ← Kafka + Redis + PostgreSQL
└── MICROSERVICES-ARCHITECTURE.md  ← this file
```

---

## Service Specifications

### 1. Bike Service (Port 8081)

**Owns:** Product catalog, stock levels  
**Database:** PostgreSQL (`bike_db`, port 5433)  
**Dependencies:** Quarkus REST, Hibernate/Panache, Kafka

**REST API:**

| Method | Path | Description |
|---|---|---|
| GET | `/bikes` | List all bikes (exposes `availableStock`) |
| GET | `/bikes/{id}` | Get single bike details |

**Key Implementation — Stock with Semantic Locks:**

```java
@MappedSuperclass
public abstract class Product {
    private int availableStock;   // what users see
    private int reservedStock;    // locked during saga
}
```

Three stock operations (all use `LockModeType.PESSIMISTIC_WRITE`):

| Operation | availableStock | reservedStock | When |
|---|---|---|---|
| `reserveStock()` | −qty | +qty | Saga starts (checkout) |
| `confirmReservation()` | unchanged | −qty | Saga succeeds (order confirmed) |
| `releaseStock()` | +qty | −qty | Saga fails (rollback) |

**Kafka Channels:**

| Topic | Direction | Description |
|---|---|---|
| `stock.reserve` | Incoming | Reserve stock for an order |
| `stock.release` | Incoming | Release stock (saga compensation) |
| `stock.confirm` | Incoming | Finalize reservation (saga success) |
| `stock.reserved` | Outgoing | Reservation succeeded |
| `stock.insufficient` | Outgoing | Reservation failed |

**Startup Seeding:** 4 sample bikes are seeded on dev startup via `Startup.java`.

---

### 2. Cart Service (Port 8082)

**Owns:** Shopping cart state  
**Storage:** Redis (no JPA)  
**Dependencies:** Quarkus REST, Quarkus Redis, Kafka

**REST API:**

| Method | Path | Description |
|---|---|---|
| GET | `/cart` | Get cart (userId from `X-User-Id` header) |
| POST | `/cart/add` | Add bike to cart (bike data in request body) |
| POST | `/cart/delete/{cartItemId}` | Remove item |
| POST | `/cart/updateQuantity/{itemId}/{type}` | Inc/dec quantity |
| GET | `/cart/checkout` | Get checkout summary |

**Redis Data Model:**

```
Key:   "cart:{userId}"
Value: JSON string of Cart object
TTL:   7 days (auto-expire abandoned carts)
```

Cart items store **bike snapshots** (bikeId, model, price, imageSource) — not JPA references.

**Kafka Channels:**

| Topic | Direction | Description |
|---|---|---|
| `cart.clear` | Incoming | Clear cart after successful order |

**Design Note:** Stock validation does NOT happen at add-to-cart. It happens at checkout via the Saga.
The frontend already has bike details from the Bike microfrontend, so it sends them in the POST body (no HTTP call to Bike Service).

---

### 3. Order Service (Port 8083)

**Owns:** Orders, shipping, saga orchestration  
**Database:** PostgreSQL (`order_db`, port 5434)  
**Dependencies:** Quarkus REST, Hibernate/Panache, Kafka, REST Client, SSE

**REST API:**

| Method | Path | Description |
|---|---|---|
| POST | `/order/finalise` | Place order (returns **202 Accepted**) |
| GET | `/order/history` | User's order history |
| GET | `/order/status/{txn}` | SSE stream for live status updates |

**Order Entity (Decoupled):**

```java
@Entity
public class Order {
    private Long id;
    private String userId;           // simple string, NOT a JPA FK
    private String transaction;      // UUID (saga correlation ID)
    private OrderState orderState;   // PENDING → CONFIRMED → PROCESSING → SHIPPING → COMPLETED
    private AcquireType acquireType; // BUY or RENT
    private ShippingAddress shippingAddress;
    private List<ShippingItem> shippingItems;  // bike snapshots, NOT FK to bike table
    private BigDecimal totalPrice;
}
```

**OrderState Lifecycle:**

```
PENDING → CONFIRMED → PROCESSING → SHIPPING → COMPLETED
    │                                              
    └─→ FAILED (stock insufficient)
    └─→ CANCELED
```

**Kafka Channels:**

| Topic | Direction | Description |
|---|---|---|
| `stock.reserve` | Outgoing | Request stock reservation |
| `stock.confirm` | Outgoing | Confirm reservation (saga success) |
| `cart.clear` | Outgoing | Clear user's cart (saga success) |
| `stock.reserved` | Incoming | Reservation succeeded |
| `stock.insufficient` | Incoming | Reservation failed |

**REST Client:** Order Service fetches the user's cart from Cart Service via MicroProfile REST Client (`CartRestClient`) at checkout time.

---

## Checkout Saga Flow

### Happy Path

```
Frontend                Order Service           Kafka              Bike Service         Cart Service
   │                         │                    │                      │                    │
   │ POST /order/finalise    │                    │                      │                    │
   │────────────────────────→│                    │                      │                    │
   │                         │ Create Order       │                      │                    │
   │                         │ (status=PENDING)   │                      │                    │
   │  202 Accepted           │                    │                      │                    │
   │←────────────────────────│                    │                      │                    │
   │                         │                    │                      │                    │
   │ GET /order/status/{txn} │                    │                      │                    │
   │────────────────────────→│ SSE connection     │                      │                    │
   │                         │                    │                      │                    │
   │                         │ ──── stock.reserve ──────────────────────→│                    │
   │                         │                    │                      │ PESSIMISTIC LOCK   │
   │                         │                    │                      │ availableStock -= n│
   │                         │                    │                      │ reservedStock  += n│
   │                         │ ←── stock.reserved ──────────────────────│                    │
   │                         │                    │                      │                    │
   │                         │ Update: CONFIRMED  │                      │                    │
   │ SSE: CONFIRMED          │                    │                      │                    │
   │←─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│                    │                      │                    │
   │                         │                    │                      │                    │
   │                         │ ──── stock.confirm ──────────────────────→│                    │
   │                         │                    │                      │ reservedStock  -= n│
   │                         │                    │                      │ (sold!)            │
   │                         │                    │                      │                    │
   │                         │ ──── cart.clear ────────────────────────────────────────────── →│
   │                         │                    │                      │                    │ DEL cart:{userId}
   │                         │                    │                      │                    │
   │                         │ (simulate processing)                     │                    │
   │ SSE: PROCESSING         │                    │                      │                    │
   │←─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│                    │                      │                    │
   │ SSE: SHIPPING           │                    │                      │                    │
   │←─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│                    │                      │                    │
   │ SSE: COMPLETED          │                    │                      │                    │
   │←─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│                    │                      │                    │
```

### Failure Path (Stock Insufficient)

```
   │                         │ ──── stock.reserve ──────────────────────→│
   │                         │                    │                      │ Stock check fails
   │                         │ ←── stock.insufficient ─────────────────│
   │                         │                    │                      │
   │                         │ Update: FAILED     │                      │
   │ SSE: FAILED             │                    │                      │
   │←─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│                    │                      │
   │                         │                    │                      │
   │ (Cart NOT cleared — user can modify and retry)                      │
```

### Partial Failure (Multi-Item Compensation)

If the cart has items A, B, C and item C is out of stock:
1. A and B are reserved successfully
2. C fails → Bike Service automatically **releases A and B** (compensation within same consumer)
3. `stock.insufficient` is published for the whole order
4. Order Service marks order as FAILED

---

## Semantic Lock Pattern

### Why Semantic Locks?

The standard Saga pattern has a dirty-read problem: during the reservation window, other services
could read intermediate state. For example, if stock is decremented but the order later fails,
other users might see artificially low stock.

### How It Works

Instead of a single `stock` field, the `Product` entity has two:

| Field | Meaning | Visible to users? |
|---|---|---|
| `availableStock` | Stock available for purchase | ✅ Yes (exposed via BikeDTO) |
| `reservedStock` | Stock locked during an active saga | ❌ No (internal bookkeeping) |

```
                    availableStock    reservedStock    Total (logical)
                    ──────────────    ─────────────    ───────────────
Initial state:      10                0                10
After reserve(3):   7                 3                10
After confirm(3):   7                 0                7  (3 sold)
  — OR —
After release(3):   10                0                10 (rollback)
```

**Key benefit:** Other users always see `availableStock = 7` during the saga.
They are never exposed to the uncertainty of the reservation. If the saga fails,
`availableStock` goes back to 10 — no dirty read ever happened.

---

## Kafka Topics

| Topic | Producer | Consumer | Payload Schema |
|---|---|---|---|
| `stock.reserve` | Order Service | Bike Service | `{ orderId, items: [{bikeId, quantity}] }` |
| `stock.reserved` | Bike Service | Order Service | `{ orderId, success: true }` |
| `stock.insufficient` | Bike Service | Order Service | `{ orderId, success: false, reason }` |
| `stock.release` | Order Service | Bike Service | `{ orderId, items: [{bikeId, quantity}] }` |
| `stock.confirm` | Order Service | Bike Service | `{ orderId, items: [{bikeId, quantity}] }` |
| `cart.clear` | Order Service | Cart Service | `{ userId }` |

**Total: 6 topics**

### Kafka Config (Local Dev)

- Single broker, KRaft mode (no Zookeeper)
- `confluentinc/cp-kafka:7.6.0`
- Port: `9092`

---

## SSE (Server-Sent Events)

The Order Service streams real-time status updates to the frontend:

```
GET /order/status/{transactionId}
Content-Type: text/event-stream

data: {"orderId":"abc-123","state":"CONFIRMED","message":"Stock reserved"}
data: {"orderId":"abc-123","state":"PROCESSING","message":"Order is being processed"}
data: {"orderId":"abc-123","state":"SHIPPING","message":"Order has been shipped"}
data: {"orderId":"abc-123","state":"COMPLETED","message":"Order delivered"}
```

**Implementation:**
- `OrderSseManager` maintains a `ConcurrentHashMap<String, List<MultiEmitter<OrderStatusEvent>>>>`
- `subscribe(transactionId)` returns a `Multi<OrderStatusEvent>`
- `broadcast(transactionId, state, message)` pushes to all connected clients
- Stream auto-completes on terminal states: `COMPLETED`, `FAILED`, `CANCELED`
- Processing simulation uses a background thread with 3-second delays between states

---

## Docker Compose (Local Dev)

```yaml
services:
  kafka:        # KRaft mode, no Zookeeper, port 9092
  redis:        # Redis 7 Alpine, port 6379
  postgres-bike:   # PostgreSQL 16, port 5433, db: bike_db
  postgres-order:  # PostgreSQL 16, port 5434, db: order_db
```

### Port Mapping

| Service | Port |
|---|---|
| Bike Service | 8081 |
| Cart Service | 8082 |
| Order Service | 8083 |
| Kafka | 9092 |
| Redis | 6379 |
| PostgreSQL (bike) | 5433 |
| PostgreSQL (order) | 5434 |

### Running Locally

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Start each service (in separate terminals)
cd bike-service  && ./mvnw quarkus:dev
cd cart-service  && ./mvnw quarkus:dev
cd order-service && ./mvnw quarkus:dev
```

---

## AWS EKS Deployment Notes

The Quarkus code is **fully portable to EKS** — no code changes required, only configuration.

### Infrastructure Mapping

| Local (docker-compose) | AWS Equivalent | Config Property |
|---|---|---|
| `localhost:9092` (Kafka) | Amazon MSK | `kafka.bootstrap.servers` |
| `localhost:6379` (Redis) | ElastiCache for Redis | `quarkus.redis.hosts` |
| `localhost:5433` (PG bike) | Amazon RDS (PostgreSQL) | `quarkus.datasource.jdbc.url` |
| `localhost:5434` (PG order) | Amazon RDS (PostgreSQL) | `quarkus.datasource.jdbc.url` |

### MSK Authentication

Local Kafka has no auth. MSK requires IAM auth or SASL/SCRAM:

```properties
# Additional properties for MSK
kafka.security.protocol=SASL_SSL
kafka.sasl.mechanism=AWS_MSK_IAM
kafka.sasl.jaas.config=software.amazon.msk.auth.iam.IAMLoginModule required;
```

Plus the `aws-msk-iam-auth` dependency in `pom.xml`.

### Kubernetes Configuration

Use ConfigMaps/Secrets to override `application.properties` per environment.
Quarkus reads environment variables natively:

```yaml
# Example K8s deployment env vars
env:
  - name: KAFKA_BOOTSTRAP_SERVERS
    value: "b-1.mycluster.xxx.kafka.us-east-1.amazonaws.com:9098"
  - name: QUARKUS_REDIS_HOSTS
    value: "redis://my-cluster.xxx.cache.amazonaws.com:6379"
  - name: QUARKUS_DATASOURCE_JDBC_URL
    value: "jdbc:postgresql://my-rds.xxx.rds.amazonaws.com:5432/bike_db"
```

### What Stays the Same

- All Kafka consumers/producers (SmallRye Reactive Messaging)
- All Redis commands (Quarkus Redis client)
- All JPA/Hibernate entities and repositories
- All REST endpoints and SSE streams
- The saga orchestration logic

---

## Technical Notes & Gotchas

### Quarkus 3.x Kafka Extension Rename
The Kafka extension artifact was renamed in Quarkus 3.x:
- ❌ `quarkus-smallrye-reactive-messaging-kafka` (old, not in BOM)
- ✅ `quarkus-messaging-kafka` (correct for Quarkus 3.19.1)

### Java 17 Constraint
`Thread.ofVirtual()` is Java 21+. The codebase uses `new Thread(...).start()` for background
processing simulation.

### Maven Wrapper
Each service needs its own copy of `.mvn/`, `mvnw`, `mvnw.cmd`. These were copied from the
existing `api/` monolith.

### Cart Redis Storage
Cart is stored as a JSON string via `setex` with 7-day TTL. Uses `ValueCommands<String, String>`.
Not JPA entities — no Hibernate involved.

### REST Client (Order → Cart)
Order Service fetches the cart at checkout via MicroProfile REST Client:
```properties
quarkus.rest-client.cart-service.url=http://localhost:8082
```

### Monolith Entity Couplings (Broken)
| Monolith Coupling | Microservice Solution |
|---|---|
| `CartItem` → `Bike` (`@OneToOne`) | Cart stores bike snapshots (bikeId, model, price) |
| `ShippingItem` → `Bike` (`@ManyToOne`) | Order stores bike snapshots (no FK) |
| `Order` → `User` (`@ManyToOne`) | Order stores `userId` as String |
| `User` → `Cart` (`@OneToOne`) | Cart keyed by userId in Redis |
| `CartService` → `BikeRepository` | Event-driven (Kafka) |
| `OrderService` → `UserService` | Eliminated (X-User-Id header) |

---

## Files Created

### Bike Service (11 files)
| File | Purpose |
|---|---|
| `pom.xml` | Quarkus REST, Hibernate/Panache, PostgreSQL, Kafka |
| `application.properties` | Port 8081, DB, Kafka topics |
| `model/Product.java` | Abstract base: availableStock + reservedStock |
| `model/Bike.java` | Entity extending Product |
| `repository/BikeRepository.java` | Panache repository |
| `service/BikeService.java` | Reserve/confirm/release with pessimistic locks |
| `controller/BikeController.java` | GET /bikes, GET /bikes/{id} |
| `controller/dto/BikeDTO.java` | Exposes availableStock |
| `event/StockReservationConsumer.java` | Consumes stock.reserve, stock.release, stock.confirm |
| `Startup.java` | Seeds 4 sample bikes |

### Cart Service (9 files)
| File | Purpose |
|---|---|
| `pom.xml` | Quarkus REST, Redis, Kafka |
| `application.properties` | Port 8082, Redis, Kafka |
| `model/Cart.java` | POJO: getTotal(), getRentTotal(), isEmpty() |
| `model/CartItem.java` | POJO: bike snapshot fields |
| `service/CartService.java` | Redis ops (setex, 7-day TTL) |
| `controller/CartController.java` | Full CRUD with X-User-Id header |
| `controller/dto/CartDTO.java` | Response DTO |
| `controller/dto/CheckoutDTO.java` | Checkout summary DTO |
| `controller/dto/AddToCartRequest.java` | POST body for add-to-cart |
| `event/CartEventConsumer.java` | Consumes cart.clear |

### Order Service (16 files)
| File | Purpose |
|---|---|
| `pom.xml` | Quarkus REST, Hibernate, Kafka, REST Client |
| `application.properties` | Port 8083, DB, Kafka, REST client URL |
| `model/Order.java` | userId as String, starts PENDING |
| `model/ShippingItem.java` | Bike snapshots (no FK) |
| `model/ShippingAddress.java` | Embedded address |
| `model/OrderState.java` | Enum: PENDING → COMPLETED / FAILED |
| `model/AcquireType.java` | Enum: BUY, RENT |
| `repository/OrderRepository.java` | findByTransaction() |
| `service/OrderService.java` | placeOrder(), updateOrderState() |
| `service/OrderSagaOrchestrator.java` | Full saga lifecycle |
| `service/OrderSseManager.java` | SSE connection manager |
| `service/OrderStatusEvent.java` | SSE payload POJO |
| `service/CartRestClient.java` | MP REST Client interface |
| `event/OrderEventConsumer.java` | Consumes stock.reserved/insufficient |
| `controller/OrderController.java` | POST /order/finalise, GET /history |
| `controller/OrderSseController.java` | SSE stream endpoint |
| `controller/dto/OrderDTO.java` | Response DTO |
| `controller/dto/OrderRequestDTO.java` | Request DTO |

### Infrastructure (1 file)
| File | Purpose |
|---|---|
| `docker-compose.yml` | Kafka (KRaft), Redis, 2x PostgreSQL |

---

## Implementation Status

| Phase | Task | Status |
|---|---|---|
| 1 | Docker Compose infrastructure | ✅ Done |
| 1 | Bike Service scaffold | ✅ Done |
| 1 | Cart Service scaffold | ✅ Done |
| 1 | Order Service scaffold | ✅ Done |
| 2 | Bike models (Product + Bike) | ✅ Done |
| 2 | Bike REST API | ✅ Done |
| 2 | Bike Kafka stock consumer | ✅ Done |
| 2 | Bike seed data | ✅ Done |
| 3 | Cart Redis model | ✅ Done |
| 3 | Cart REST API | ✅ Done |
| 3 | Cart Kafka consumer | ✅ Done |
| 4 | Order models (decoupled) | ✅ Done |
| 4 | Order REST API | ✅ Done |
| 4 | Order Saga Orchestrator | ✅ Done |
| 4 | Order SSE controller | ✅ Done |
| 4 | Order ↔ Cart REST client | ✅ Done |
| 5 | CORS configuration | ✅ Done |
| 5 | Semantic Locks | ✅ Done |
| 5 | Integration test (E2E) | ⬜ Pending |

**Build Status:** All 3 services compile successfully with `./mvnw compile`.

---

## Design Decisions Log

### 1. Why Kafka over RabbitMQ?
Kafka provides durable, replayable event logs. Good fit for the saga pattern where we need
guaranteed delivery and potential message replay. Also better for thesis demonstration of
event-driven architecture.

### 2. Why Redis for Cart (not PostgreSQL)?
Carts are ephemeral — they don't need ACID transactions, complex queries, or long-term durability.
Redis with TTL auto-cleans abandoned carts. Simpler model (JSON blob) vs JPA entity lifecycle.

### 3. Why Semantic Locks over plain Saga?
The standard saga has a **dirty read** problem. Between `stock.reserve` and `stock.confirm`/`stock.release`,
another user might see inconsistent stock. Semantic locks split stock into `availableStock` (always consistent,
always visible) and `reservedStock` (internal bookkeeping). Users only ever see `availableStock`.

### 4. Why `X-User-Id` header instead of a User Service?
The thesis focuses on microfrontends, not auth. A full user/auth service adds complexity without
supporting the thesis argument. The `X-User-Id` header is a simple, pragmatic approach.

### 5. Why 202 Accepted (not 200 OK) for checkout?
The checkout is asynchronous — the order is placed but not yet confirmed (stock reservation happens
via Kafka). Returning 202 with a transaction ID is semantically correct: "I accepted your request
and will process it." The client then subscribes to SSE for real-time updates.

### 6. Why Pessimistic Lock inside Bike Service?
Even though services communicate via Kafka (async), the stock update within the Bike Service DB
must be atomic. `SELECT ... FOR UPDATE` (pessimistic lock) prevents two concurrent saga reservations
from overselling the same bike.

### 7. Why store bike snapshots in Cart and Order?
Decoupling. If Bike Service changes a price, existing carts and past orders should NOT be affected.
Storing snapshots (bikeId, model, price at time of action) ensures data independence between services.
