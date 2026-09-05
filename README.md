# 📚 BookShop E-Commerce Backend Platform

A robust, full-featured E-Commerce backend system built with Java Spring Boot. The platform provides end-to-end e-commerce workflows, JWT authentication with Refresh Token rotation, real-time live support chat via WebSocket, VNPay payment gateway integration, and asynchronous mail task execution.

---

## 🚀 Tech Stack

- **Core Framework:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA, Spring MVC
- **Security:** JWT (Access & Refresh Tokens), BCrypt Password Hashing, Role-Based Access Control (RBAC)
- **Database & Caching:** MySQL, Redis (OTP Caching & JWT Token Blacklisting)
- **Real-Time Messaging:** WebSocket (STOMP Protocol, SockJS)
- **External Integrations:** VNPay Gateway (HMAC-SHA512 Verification), Cloudinary SDK (Media Storage), JavaMailSender
- **Build & DevOps Tools:** Maven, Lombok, Git/GitHub, Postman

---

## ✨ Key Features

### 🔐 Authentication & Security
- **Stateless JWT Security:** Implemented Access & Refresh Token rotation mechanism.
- **Token Revocation:** Instant logout support using Redis-backed token blacklisting.
- **Account Activation:** 6-digit OTP email verification for user registration.
- **Role-Based Access Control (RBAC):** Differentiated access levels for `ROLE_USER` and `ROLE_ADMIN`.

### 🛒 Core E-Commerce Operations
- **Product Catalog Management:** Full CRUD operations for books, authors, categories, and publishers with soft-delete logic (`DISCONTINUED`).
- **Dynamic Search & Filtering:** Multi-criteria search (keyword, category, price range) optimized with server-side pagination via `Spring Data Pageable`.
- **Cart & Order Lifecycle:** Stock validation, cart management, order status tracking, and return/refund processing with image proof uploaded to Cloudinary.
- **Verified Product Reviews:** Purchase-verified product review and rating system.

### ⚡ Real-Time & Integrations
- **Customer Support Chat:** Instant 1-on-1 support chat between users and admins using WebSocket & STOMP with dynamic room routing.
- **Payment Gateway:** Online transaction processing with VNPay Sandbox checksum validation.
- **Asynchronous Execution:** Non-blocking email sending using `ThreadPoolTaskExecutor` (`@Async`).

---

## ⚙️ Getting Started

### Prerequisites
- JDK 17+
- MySQL 8.x
- Redis Server
- Maven 3.x

### Local Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/your-username/bookshop-backend.git](https://github.com/your-username/bookshop-backend.git)
   cd bookshop-backend