# 📚 BookShop - Backend REST API (E-Commerce Platform)

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL 8" />
  <img src="https://img.shields.io/badge/Redis-Caching-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/JWT-Tokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge&logo=socketdotio&logoColor=white" alt="WebSocket" />
  <img src="https://img.shields.io/badge/VNPay-Sandbox-005BAA?style=for-the-badge&logo=vnpay&logoColor=white" alt="VNPay" />
  <img src="https://img.shields.io/badge/Cloudinary-Media-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white" alt="Cloudinary" 
</p>

> A Backend RESTful API for an online book e-commerce platform built with **Spring Boot** and **Java 17**. Features **JWT authentication with Refresh Token rotation**, **Redis token blacklisting**, **VNPay payment gateway integration**, **WebSocket STOMP live support chat**, **Cloudinary image uploads**, and non-blocking **asynchronous email processing**.

---

## 🌟 Features

- 🔐 **Authentication & Security**: User registration with 6-digit OTP email verification, JWT Access & Refresh Token rotation, instant logout via Redis token blacklisting, and Role-Based Access Control (`ROLE_ADMIN`, `ROLE_USER`).
- 📖 **Catalog & Inventory**: CRUD management for books, categories, authors, and publishers. Supports soft-deletion (`DISCONTINUED`) to maintain order history integrity.
- 🔍 **Search & Filtering**: Multi-criteria dynamic searching (by keyword, category, price range) optimized with server-side pagination and sorting (`Spring Data Pageable`).
- 🛒 **Cart & Order Processing**: Real-time stock availability check, cart management, order lifecycle tracking, and return/refund processing with Cloudinary proof images.
- 💬 **Real-Time Support Chat**: Dynamic 1-on-1 customer support chat using **WebSocket & STOMP protocol** (`/topic`, `/queue`) with chat history persistence.
- 💳 **Payment & Integrations**: Secure online checkout sandbox with **VNPay** (HMAC-SHA512 checksum verification) and **Cloudinary SDK** for media uploads.
- ⚡ **Performance & Infrastructure**: ThreadPoolTaskExecutor for non-blocking `@Async` email delivery and automated **JPA Auditing** (`AuditorAware`) via `SecurityContextHolder`.

---

## 🛠️ Tech Stack

| Category | Technology / Library | Description |
| :--- | :--- | :--- |
| **Language & Framework** | Java 17, Spring Boot 3.x, Spring Data JPA | Core backend framework and ORM persistence |
| **Database & Caching** | MySQL 8.x, Redis (Lettuce) | Database storage, OTP caching, and JWT token revocation |
| **Security** | Spring Security, Nimbus JWT | Stateless authentication, HS512 JWT signing, and RBAC |
| **Real-Time Messaging** | WebSocket, STOMP, SockJS | Instant customer support chat channels |
| **External Services** | VNPay Gateway, Cloudinary SDK, JavaMailSender | Payment processing, cloud media storage, and email delivery |
| **Build & Utilities** | Maven, Lombok, MapStruct | Dependency management and object mapping |

---

## ⚡ Quick Start

### 1. Prerequisites
Ensure you have the following installed locally:
- **JDK 17+**
- **MySQL 8.x**
- **Redis Server**
- **Maven 3.x**

---

### 2. Configure Environment Properties
Update your `src/main/resources/application.properties` (or `application.yml`) with local database credentials:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/bookshop_db
spring.datasource.username=root
spring.datasource.password=your_password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Secret
jwt.secret=YourSuperSecretKeyHS512AlgorithmMustBeLongEnoughForSecurity1234567890

# Cloudinary
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET

# VNPay
vnpay.tmnCode=YOUR_TMN_CODE
vnpay.hashSecret=YOUR_HASH_SECRET
vnpay.payUrl=[https://sandbox.vnpayment.vn/paymentv2/vpcpay.html](https://sandbox.vnpayment.vn/paymentv2/vpcpay.html)
vnpay.returnUrl=http://localhost:8080/api/orders/vnpay-return