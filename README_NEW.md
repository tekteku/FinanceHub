# 💰 FinanceHub - Comprehensive Finance Management Platform

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Angular](https://img.shields.io/badge/Angular-18.2-red)
![Java](https://img.shields.io/badge/Java-17-orange)
![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

**A modern, full-stack financial management platform built with industry-standard technologies and best practices.**

[Features](#-key-features) • [Architecture](#️-architecture) • [Getting Started](#-getting-started) • [API Documentation](#-api-documentation)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#️-technology-stack)
- [Architecture](#️-architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Security](#-security)
- [Contributing](#-contributing)

---

## 🎯 Overview

FinanceHub is a revolutionary finance management platform designed to help users track their finances, manage budgets, analyze spending patterns, and make informed financial decisions. Built with modern technologies and following industry best practices, this project demonstrates proficiency in full-stack development, RESTful API design, security implementation, and cloud deployment.

### Project Highlights

- ✅ **Enterprise-Grade Architecture**: Clean layered architecture with separation of concerns
- ✅ **Security First**: JWT-based authentication, BCrypt password encryption, RBAC
- ✅ **RESTful API**: Well-documented REST APIs with OpenAPI/Swagger specification
- ✅ **Comprehensive Testing**: Unit tests, integration tests with 70%+ code coverage
- ✅ **Docker Support**: Fully containerized application with Docker Compose
- ✅ **CI/CD Ready**: GitHub Actions workflows for automated testing and deployment
- ✅ **Responsive UI**: Modern Angular application with Material Design
- ✅ **Real-time Analytics**: Interactive charts and financial insights

---

## 🚀 Key Features

### Account Management
- ✨ Multiple account types (Checking, Savings, Credit Card, Investment, etc.)
- 💵 Multi-currency support
- 📊 Real-time balance tracking
- 🎨 Custom account colors and icons

### Transaction Tracking
- 💸 Income and expense tracking
- 🏷️ Category-based organization
- 📅 Date range filtering and search
- 📝 Detailed transaction notes
- 🔄 Recurring transaction templates

### Budget Management
- 🎯 Category-specific budgets
- ⏰ Multiple period types (Weekly, Monthly, Quarterly, Yearly)
- 🚨 Alert thresholds and notifications
- 📈 Budget vs. actual spending analysis

### Analytics & Reporting
- 📊 Interactive charts and graphs
- 🔍 Spending pattern analysis
- 💹 Income vs. expense trends
- 📉 Category-wise breakdowns
- 📆 Custom date range reports

### Security Features
- 🔐 JWT token-based authentication
- 🔒 BCrypt password hashing (12 rounds)
- 🛡️ Role-based access control (RBAC)
- 🚫 XSS and CSRF protection
- 📝 Comprehensive audit logging

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Security**: Spring Security 6, JWT
- **Database**: PostgreSQL 15
- **Caching**: Redis
- **ORM**: Hibernate/JPA
- **Validation**: Bean Validation (JSR 380)
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ

### Frontend
- **Framework**: Angular 18
- **Language**: TypeScript 5.5
- **UI Components**: Angular Material
- **Charts**: Chart.js
- **State Management**: RxJS
- **HTTP Client**: HttpClient with Interceptors
- **Routing**: Angular Router with Guards
- **Build Tool**: Angular CLI

### DevOps & Infrastructure
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Monitoring**: Spring Boot Actuator, Prometheus
- **Database**: PostgreSQL 15

---

## 🏗️ Architecture

### Backend Architecture

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                │
│  (REST Controllers + Exception Handlers)    │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│            Service Layer                    │
│  (Business Logic + Validation + Logging)    │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          Repository Layer                   │
│  (JPA Repositories + Custom Queries)        │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│            Database Layer                   │
│         (PostgreSQL + Redis)                │
└─────────────────────────────────────────────┘
```

### Security Flow

```
Client Request
     │
     ▼
JwtAuthenticationFilter ──► Validate Token
     │                            │
     ▼                            ▼
 Valid Token?              Invalid Token
     │                            │
     ▼                            ▼
Set Authentication          Return 401
     │
     ▼
Process Request
     │
     ▼
Send Response
```

---

## 📁 Project Structure

```
FinanceHub/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/financehub/
│   │   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   ├── mapper/          # Entity-DTO mappers
│   │   │   │   ├── repository/      # JPA repositories
│   │   │   │   ├── security/        # Security components
│   │   │   │   └── service/         # Business logic
│   │   │   └── resources/
│   │   │       └── application.yml   # Application config
│   │   └── test/                    # Unit & Integration tests
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/         # Angular components
│   │   │   ├── guards/            # Route guards
│   │   │   ├── interceptors/      # HTTP interceptors
│   │   │   ├── models/            # TypeScript interfaces
│   │   │   └── services/          # API services
│   │   ├── environments/          # Environment configs
│   │   └── styles.scss           # Global styles
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
│
├── docker-compose.yml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 20 or higher
- PostgreSQL 15
- Docker & Docker Compose (optional)
- Maven 3.9+

### Installation

#### Option 1: Using Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/financehub.git
cd financehub

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8081/api
# Swagger UI: http://localhost:8081/swagger-ui.html
```

#### Option 2: Manual Setup

**Backend Setup:**

```bash
# Navigate to backend directory
cd backend

# Install dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/financehub-backend-1.0.0.jar
```

**Frontend Setup:**

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Run development server
npm start

# Build for production
npm run build
```

### Default Credentials

```
Email: admin@financehub.com
Password: Admin123!
```

---

## 📚 API Documentation

### Accessing API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Key API Endpoints

#### Authentication
```
POST   /api/auth/login       # User login
POST   /api/auth/register    # User registration
POST   /api/auth/logout      # User logout
```

#### Accounts
```
GET    /api/accounts              # Get all accounts
GET    /api/accounts/active       # Get active accounts
GET    /api/accounts/{id}         # Get account by ID
POST   /api/accounts              # Create account
PUT    /api/accounts/{id}         # Update account
DELETE /api/accounts/{id}         # Delete account (soft delete)
GET    /api/accounts/balance/total # Get total balance
```

#### Transactions
```
GET    /api/transactions                    # Get all transactions (paginated)
GET    /api/transactions/recent             # Get recent transactions
GET    /api/transactions/{id}               # Get transaction by ID
GET    /api/transactions/type/{type}        # Get by type
GET    /api/transactions/account/{id}       # Get by account
POST   /api/transactions                    # Create transaction
PUT    /api/transactions/{id}               # Update transaction
DELETE /api/transactions/{id}               # Delete transaction
GET    /api/transactions/range              # Get by date range
GET    /api/transactions/income/total       # Get total income
GET    /api/transactions/expenses/total     # Get total expenses
```

#### Categories
```
GET    /api/categories          # Get all categories
GET    /api/categories/{id}     # Get category by ID
GET    /api/categories/type/{type} # Get by type
POST   /api/categories          # Create category
PUT    /api/categories/{id}     # Update category
DELETE /api/categories/{id}     # Delete category
```

---

## 🧪 Testing

### Backend Tests

```bash
cd backend

# Run all tests
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Frontend Tests

```bash
cd frontend

# Run unit tests
npm test

# Run with coverage
npm run test:coverage

# Run e2e tests
npm run e2e
```

---

## 🔒 Security

### Implemented Security Measures

1. **Authentication**: JWT-based stateless authentication
2. **Authorization**: Role-based access control (RBAC)
3. **Password Security**: BCrypt hashing with 12 rounds
4. **Input Validation**: Bean Validation on all DTOs
5. **SQL Injection Prevention**: JPA parameterized queries
6. **XSS Protection**: Content Security Policy headers
7. **CSRF Protection**: Stateless tokens
8. **Audit Logging**: Comprehensive activity logging
9. **Error Handling**: Global exception handling without info leakage
10. **Secure Headers**: CORS, Security Headers configured

---

## 👥 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**tekteku**
- GitHub: [@tekteku](https://github.com/tekteku)
- Email: support@financehub.com
- Date: November 2025

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Angular team for the powerful frontend framework
- All open-source contributors

---

<div align="center">

**Built with ❤️ by tekteku**

⭐ If you find this project useful, please consider giving it a star!

</div>
