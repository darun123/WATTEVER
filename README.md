# WaterX PowerBank Rental System

A QR-code-triggered power bank rental system built with Spring Boot microservices and React JS.

## Architecture

```
[QR Code] → [React Frontend] → [API Gateway :8080]
                                      ├── Station Service :8081
                                      ├── Rental Service  :8082
                                      └── Payment Service :8083 (Razorpay)
```

## Quick Start (Local Development)

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+
- Your Razorpay Test API keys

### 1. Backend — Start all services

Open **4 terminal windows** and run each:

```bash
# Terminal 1 — API Gateway
cd backend/api-gateway
mvn spring-boot:run

# Terminal 2 — Station Service
cd backend/station-service
mvn spring-boot:run

# Terminal 3 — Rental Service
cd backend/rental-service
mvn spring-boot:run

# Terminal 4 — Payment Service
# First, add your Razorpay keys to backend/payment-service/src/main/resources/application.yml
cd backend/payment-service
mvn spring-boot:run
```

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

### 3. Test the Flow

Open your browser to:
```
http://localhost:5173/rent?station=ST001&slot=1
```

This simulates scanning a QR code for **Station ST001, Slot 1** (Mumbai Central Mall).

## Razorpay Configuration

Edit `backend/payment-service/src/main/resources/application.yml`:

```yaml
razorpay:
  key-id: YOUR_RAZORPAY_KEY_ID       # rzp_test_XXXXXXXXXXXX
  key-secret: YOUR_RAZORPAY_KEY_SECRET
```

### Test Card
- Card: `4111 1111 1111 1111`
- Expiry: Any future date
- CVV: Any 3 digits
- OTP: `1234` (test mode)

## QR Code Format

Each QR code should encode a URL in this format:
```
https://yourdomain.com/rent?station={STATION_ID}&slot={SLOT_NUMBER}
```

**Example:**
```
https://yourdomain.com/rent?station=ST001&slot=1
```

## Sample Stations (Pre-loaded)

| Station ID | Name | Location | Price/hr |
|-----------|------|----------|----------|
| ST001 | Mumbai Central Mall | Ground Floor, Gate 3 | ₹10 |
| ST002 | Andheri Metro Station | Exit Gate 2 | ₹8 |
| ST003 | Phoenix Mall Kurla | Food Court Level | ₹12 |

## API Reference

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/stations/{stationId}/slot/{slotNumber}` | Get slot info & pricing |
| POST | `/api/rentals/initiate` | Create a rental session |
| POST | `/api/payments/create-order` | Create Razorpay order |
| POST | `/api/payments/verify` | Verify payment & trigger release |

## Physical Station Release API

When a payment is verified, the system calls your station's release API:

```http
POST {station.release_api_url}
X-API-Key: {station.release_api_key}
Content-Type: application/json

{
  "slotNumber": 1,
  "rentalId": "uuid-here",
  "action": "RELEASE"
}
```

To configure: Update the `release_api_url` and `release_api_key` in the `stations` table (via H2 console at `http://localhost:8081/h2-console`).

## H2 Database Consoles (Local Dev)

| Service | Console URL | JDBC URL |
|---------|-------------|----------|
| Station | http://localhost:8081/h2-console | `jdbc:h2:mem:stationdb` |
| Rental | http://localhost:8082/h2-console | `jdbc:h2:mem:rentaldb` |
| Payment | http://localhost:8083/h2-console | `jdbc:h2:mem:paymentdb` |

## Project Structure

```
WATERX/
├── backend/
│   ├── api-gateway/          Spring Cloud Gateway (port 8080)
│   ├── station-service/      Station & slot management (port 8081)
│   ├── rental-service/       Rental session lifecycle (port 8082)
│   └── payment-service/      Razorpay integration (port 8083)
├── frontend/                 React + Vite (port 5173)
└── docker-compose.yml
```
