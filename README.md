# Delivery Management App v2.0

Full-stack web application for managing deliveries — packages, drivers, customers, and status tracking.

Built with **Java 17**, **SQLite**, **vanilla HTML/CSS/JS**, and **Maven**.

## Quick Start

```bash
# Option 1: Docker
docker compose up -d
# Open http://localhost:8080

# Option 2: Maven
./mvnw compile exec:java
# Open http://localhost:8080
```

## Features

- **Dashboard** — real-time stats: total, pending, in-transit, delivered
- **Package management** — add, assign to driver, mark delivered, search, filter by status
- **Driver management** — add and list drivers
- **Customer management** — add and list customers
- **REST API** — full JSON backend (see endpoints below)

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stats` | Dashboard statistics |
| GET | `/api/packages` | List all packages |
| GET | `/api/packages?status=PENDING` | Filter by status |
| GET | `/api/packages?search=term` | Search packages |
| POST | `/api/packages` | Add a package |
| PUT | `/api/packages/{id}/assign?driverId={id}` | Assign driver |
| PUT | `/api/packages/{id}/deliver` | Mark delivered |
| GET | `/api/drivers` | List drivers |
| POST | `/api/drivers` | Add a driver |
| GET | `/api/customers` | List customers |
| POST | `/api/customers` | Add a customer |

## Status Flow

```
PENDING --(assign driver)--> IN_TRANSIT --(deliver)--> DELIVERED
```

## Tech Stack

- **Backend:** Java 17, built-in HttpServer, SQLite
- **Frontend:** Vanilla HTML, CSS, JavaScript (no frameworks)
- **Build:** Maven wrapper (no installation needed)
- **Container:** Docker (multi-stage build, JRE 17)
- **CI:** GitHub Actions (build + test on push)

## Project Structure

```
src/main/java/com/delivery/
├── App.java                     # HTTP server entry point
├── model/                       # Driver, DeliveryPackage, Customer
├── dao/                         # Database, DriverDAO, PackageDAO, CustomerDAO
└── handler/                     # ApiHandler (REST), StaticFileHandler (frontend)

src/main/resources/web/
├── index.html
├── style.css
└── app.js

src/test/java/com/delivery/dao/  # Unit tests (JUnit 5)
```
