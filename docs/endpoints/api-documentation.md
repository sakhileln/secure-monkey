# secure monkey API documentation
Secure Monkey REST API endpoints, including `request/response` formats, authentication methods, and usage examples.

## Base URL
```
http://localhost:8080
```

## Authentication
The API uses JWT (JSON Web Token) based authentication:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### Authentication Endpoints

#### 1. User Registration
Register a new user account.

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "email": "sakhile@monkeyandriver.com",
  "password": "securepassword123",
  "name": "Sakhile Ndlazi"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Sakhile Ndlazi",
  "email": "sakhile@monkeyandriver.com",
  "password": "$2a$10$n49HBcmaUs0U8PsSkP8Wi.VNAUGCXF.KmkZ9P2b4/oJC46chBHg4W",
  "notificationThreshold": 5,
  "emailNotifications": true,
  "createdAt": "2025-07-29T12:17:49.736220584",
  "updatedAt": null
}
```

**Status Codes:**
- `200 OK`: User registered successfully
- `400 Bad Request`: Invalid input data
- `409 Conflict`: Email already exists

#### 2. User Login
Authenticate user and receive JWT token.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "sakhile@monkeyandriver.com",
  "password": "securepassword123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJz...U",
    "id": 1,
    "email": "sakhile@monkeyandriver.com",
    "name": "Sakhile Ndlazi",
    "type": "Bearer"
}
```

**Status Codes:**
- `200 OK`: Login successful
- `401 Unauthorized`: Invalid credentials
- `400 Bad Request`: Invalid input data

### Health Check Endpoints

#### 3. Health Check
Check the application and database connectivity status.

**Endpoint:** `GET /api/health`

**Request:** No authentication required

**Response:**
```json
"Database connection is healthy"
```

**Status Codes:**
- `200 OK`: Application and database are healthy
- `503 Service Unavailable`: Database connection failed

## Error Responses

### Validation Error
```json
{
  "timestamp": "2025-07-29T11:32:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    {
      "field": "email",
      "message": "Email should be valid"
    }
  ]
}
```

### Authentication Error
```json
{
  "timestamp": "2025-07-29T12:04:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```
