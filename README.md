# RegLog Backend — Spring Boot 3 Authentication Service

A robust, production-ready Spring Boot 3 backend implementing secure User Registration, Login, and Session Verification using **JWT (JSON Web Tokens)** stored in **HttpOnly Cookies** and **MySQL**.

## 🚀 Key Features

- **Spring Boot 3 & Java 21+**: Modern, enterprise-grade backend architecture.
- **Aiven Cloud MySQL Database**: Cloud-hosted MySQL database with full SSL/TLS encryption.
- **Spring Security 6**: Stateless authentication with custom JWT filter (`JwtAuthenticationFilter`).
- **HttpOnly Cookie Security**: Mitigates Cross-Site Scripting (XSS) by preventing JavaScript access to JWT tokens.
- **BCrypt Password Hashing**: Passwords encrypted with salted BCrypt before database persistence.
- **Spring Data JPA & Hibernate**: Automated schema generation and entity relationship mapping (`defaultdb` / `reglog_db`).
- **CORS Configured**: Pre-configured for seamless integration with frontend applications (`http://localhost:5173`).

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.4.3
- **Language**: Java 21+
- **Security**: Spring Security 6, JJWT (`io.jsonwebtoken` 0.12.6)
- **Database**: MySQL 8.0 (`reglog_db`)
- **Build Tool**: Apache Maven

---

## 📋 API Endpoints

| Method | Endpoint | Description | Auth Required | Request Body |
| :--- | :--- | :--- | :---: | :--- |
| `POST` | `/api/reg` | Register a new user | ❌ No | `{ "name", "email", "phone", "password" }` |
| `POST` | `/api/login` | Authenticate & issue HttpOnly JWT cookie | ❌ No | `{ "name", "password" }` |
| `GET` | `/api/me` | Retrieve authenticated user profile | ✅ Yes (Cookie) | *None* |
| `POST` | `/api/logout` | Clear HttpOnly JWT cookie | ❌ No | *None* |

---

## ⚙️ Configuration (`application.properties`)

```properties
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:${DB_PORT:3306}/reglog_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:your_db_username}
spring.datasource.password=${DB_PASSWORD:your_db_password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT Settings
jwt.secret=${JWT_SECRET:your_256_bit_secret_key_here}
jwt.expiration=86400000
jwt.cookie.name=jwt_token
jwt.cookie.max-age=86400
jwt.cookie.secure=false
jwt.cookie.same-site=Lax
```

---

## 🏃 Quick Start

1. Ensure MySQL is running on `localhost:3306` (or `3307`).
2. Run the application:
   ```bash
   mvn clean spring-boot:run
   ```
3. Test with cURL or Postman:
   ```bash
   curl -X POST http://localhost:8080/api/reg \
     -H "Content-Type: application/json" \
     -d '{"name":"Shivam","email":"shivam@example.com","phone":"9876543210","password":"securepassword"}'
   ```
