# OpenCutter API

Spring Boot REST API for 2D rectangular guillotine cutting stock optimization.
It computes the most material-efficient placement of target rectangular pieces
(`pieces`) onto available stock sheets (`stock`).

---

## Software Architecture & Design Rationale

The API follows a clean, decoupled **Layered Architecture** with the **Strategy
Pattern** designed for high testability, maintainability, and clear separation
of concerns.

```bash
src/main/java/com/cuttingAPI/
├── CuttingApiApplication.java
│
├── domain/                               <-- Domain Layer (No Spring dependencies)
│   ├── model/
│   │   ├── Rectangle.java                <-- model entity (piece & stock)
│   │   └── CuttingList.java              <-- aggregate model (layout, waste calculation)
│   ├── service/
│   │   └── CuttingOptimizer.java         <-- Domain service context for optimization execution
│   └── strategy/
│       ├── PackingStrategy.java           <-- Strategy pattern interface for 2D bin packing
│       └── GuillotinePackingStrategy.java <-- 2D guillotine cutting strategy implementation
│
├── application/                          <-- Application Layer (Spring @Service)
│   ├── dto/
│   │   └── CuttingRequest.java           <-- Data Transfer Object
│   └── service/
│       └── CuttingService.java           <-- Use-case orchestration & request validation
│
└── web/                                  <-- Presentation / REST Layer (Spring @RestController)
    ├── CuttingController.java            <-- REST HTTP Endpoints (/api/cut_1, /api/cut_2)
    ├── CustomException.java              <-- Business / Exception model
    └── GlobalExceptionHandler.java       <-- HTTP Status Code mapping (400, 500)
```

### Why this Layered Strategy Architecture was chosen

1. **Isolation of Business Math :** Model, Service and strategy are pure Java 21
   logic with **zero framework dependencies**. Cutting calculation rules can be
   tested (~1ms) without spinning up Spring containers.

2. **Flexibility via Strategy Pattern:** New algorithms (e.g., Genetic Algorithm
   or Non-Guillotine CNC strategies) can be plugged in without modifying the
   domain models or application services.

3. **Decoupled Application Workflow**

---

## Quick Start (Development)

### Prerequisites

- **Java 21+** (OpenJDK)
- **Maven 3.9+**

### Local Run

```bash
# Start the Spring Boot application locally (default 0.0.0.0:8080)
mvn spring-boot:run

# Or run with custom port or interface:
API_PORT=9090 SERVER_ADDRESS=0.0.0.0 mvn spring-boot:run
```

### Build JAR

```bash
# Package production JAR into target/
mvn clean package
```

---

## Testing Strategy & Organization

Tests are organized into package layers mirroring production code in
`src/test/java/com/cuttingAPI/`:

```bash
src/test/java/com/cuttingAPI/
├── domain/
│   ├── model/
│   │   ├── RectangleTest.java           <-- Fast unit tests for geometry & cloning
│   │   └── CuttingListTest.java          <-- Fast unit tests for sorting & waste scoring
│   ├── service/
│   │   └── CuttingOptimizerTest.java    <-- Domain service integration unit tests
│   └── strategy/
│       └── GuillotinePackingStrategyTest.java <-- Algorithm edge case & strategy unit tests
└── web/
    └── CuttingControllerTest.java       <-- MockMvc web layer integration tests (HTTP 200, 400, 500, CORS)
```

```bash
# Run all unit and integration tests
mvn clean test

# Run a specific test suite
mvn test -Dtest=CuttingControllerTest
```

---

## API Endpoints

All endpoints accept and return JSON payloads.

| Method | Endpoint     | Description                                       |
| :----- | :----------- | :------------------------------------------------ |
| `POST` | `/api/cut_1` | Fixed orientation (respects wood grain direction) |
| `POST` | `/api/cut_2` | Free orientation (pieces can be rotated 90°)      |

### Request Payload Format

```json
{
  "stock": [{ "x": 0, "y": 0, "width": 1000, "height": 1000, "id": 1 }],
  "pieces": [
    {
      "x": 0,
      "y": 0,
      "width": 400,
      "height": 300,
      "id": 10,
      "assignment": 0,
      "number": 1
    },
    {
      "x": 0,
      "y": 0,
      "width": 500,
      "height": 200,
      "id": 20,
      "assignment": 0,
      "number": 2
    }
  ]
}
```

### Response Payload Format

Returns a `CuttingList` object:

- **`positions`**: List of placed pieces with computed coordinates `(x, y)` and
  assigned stock sheet (`assignment`).
- **`waste`**: List of leftover rectangular waste / off-cut areas.
- **`stock`**: Sorted list of stock boards utilized.
- **`pieces`**: Any remaining pieces that could not fit onto the stock.

---

## License

This component is part of the OpenCutter project and is licensed under the
[GNU General Public License v3.0](../LICENSE).
