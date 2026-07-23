# Delivery Management App v2.0

Web-based delivery management system built with **Java + SQLite + vanilla JS**.

## Features

- **Dashboard** — real-time stats (total, pending, in-transit, delivered)
- **Package management** — add, assign to driver, mark delivered, search/filter
- **Driver management** — add and list drivers
- **Customer management** — add and list customers
- **REST API** — full JSON backend ready to be consumed by any frontend

## How to run

```bash
cd delivery-app
./mvnw compile exec:java
```

Then open http://localhost:8080

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/stats | Dashboard statistics |
| GET | /api/packages | List all packages |
| GET | /api/packages?status=PENDING | Filter by status |
| GET | /api/packages?search=term | Search packages |
| POST | /api/packages | Add a package |
| PUT | /api/packages/{id}/assign?driverId={id} | Assign driver |
| PUT | /api/packages/{id}/deliver | Mark delivered |
| GET | /api/drivers | List drivers |
| POST | /api/drivers | Add a driver |
| GET | /api/customers | List customers |
| POST | /api/customers | Add a customer |

## Tech stack

- **Backend:** Java 17, built-in HttpServer, SQLite
- **Frontend:** Vanilla HTML/CSS/JS
- **Build:** Maven wrapper (no installation needed)
