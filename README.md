# RegLog Backend — Spring Boot 3 Authentication Service

A robust, production-ready Spring Boot 3 backend implementing secure User Registration, Login, and Session Verification using **JWT (JSON Web Tokens)** stored in **HttpOnly Cookies** and **MySQL**.

## 🚀 Key Features

- **Spring Boot 3 & Java 21+**: Modern, enterprise-grade backend architecture.
- **Aiven Cloud MySQL Database**: Cloud-hosted MySQL database with full SSL/TLS encryption.
- **Spring Security 6**: Stateless authentication with custom JWT filter (`JwtAuthenticationFilter`).
- **HttpOnly Cookie Security**: Mitigates Cross-Site Scripting (XSS) by preventing JavaScript access to JWT tokens.
- **BCrypt Password Hashing**: Passwords encrypted with salted BCrypt before database persistence.
- **Spring Data JPA & Hibernate**: Automated schema generation and entity relationship mapping.
- **CORS Configured**: Pre-configured for seamless integration with frontend applications.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.4.3
- **Language**: Java 21+
- **Security**: Spring Security 6, JJWT (`io.jsonwebtoken` 0.12.6)
- **Database**: MySQL 8.0 / Aiven Cloud MySQL
- **Build Tool**: Apache Maven
- **Deployment**: Docker / Render

---

## 📋 API Endpoints

| Method | Endpoint | Description | Auth Required | Request Body |
| :--- | :--- | :--- | :---: | :--- |
| `POST` | `/api/reg` | Register a new user | ❌ No | `{ "name", "email", "phone", "password" }` |
| `POST` | `/api/login` | Authenticate & issue HttpOnly JWT cookie | ❌ No | `{ "name", "password" }` |
| `GET` | `/api/me` | Retrieve authenticated user profile | ✅ Yes (Cookie / Bearer) | *None* |
| `POST` | `/api/logout` | Clear HttpOnly JWT cookie | ❌ No | *None* |

---

## ☁️ Cloud Deployment (Render)

This repository includes a multi-stage `Dockerfile` and `render.yaml` for containerized deployment on [Render](https://render.com).

### Render Service Setup:
1. In the Render Dashboard, go to your Web Service **Settings** (or click **New + -> Web Service**).
2. Set **Runtime** to **Docker** (Render will automatically detect the `Dockerfile`).
3. Under the **Environment** tab, configure the following Environment Variables:
   - `DB_URL`: `jdbc:mysql://<your-aiven-host>:<port>/defaultdb?sslMode=REQUIRED&useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USERNAME`: `<your_db_username>`
   - `DB_PASSWORD`: `<your_db_password>`
   - `JWT_SECRET`: `<your_256_bit_secret_key>`
   - `CORS_ALLOWED_ORIGINS`: `*` (or your deployed frontend URL)
4. Click **Deploy**. Render will build the Docker container and run the Spring Boot service.

---

## 🏃 Quick Start (Local)

1. Run the application:
   ```bash
   mvn clean spring-boot:run
   ```
2. Test registration with cURL:
   ```bash
   curl -X POST http://localhost:8080/api/reg \
     -H "Content-Type: application/json" \
     -d '{"name":"Shivam","email":"shivam@example.com","phone":"9876543210","password":"securepassword"}'
   ```
