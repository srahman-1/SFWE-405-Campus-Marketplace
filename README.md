# SFWE-405-Campus-Marketplace

SFWE 405 course project: E-commerce shopping platform (Campus Marketplace) implementing a layered architecture with ORM, repositories, REST API, business rules, and user interface.

---

# Team – Team Wesley

- Alex Lopez
- Camila Inda
- Christopher Morales
- Elend Hudson
- Mateo Alvarez
- Samiur Rahman
- Wesley Nguyen
  
**Course:** SFWE 405  
**Project:** E-commerce Campus Marketplace  

---

# Campus Marketplace – Phase 1

Spring Boot backend for a campus marketplace application.

## Features (Phase 1)

- User and Product data model
- JPA entities with H2 database
- Spring Data JPA repositories
- REST API with controllers
- Tested using Postman

## Endpoints (Phase 1)

- POST /users
- GET /users
- POST /products
- GET /products

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- Postman (API testing)

---

# Phase 2 – Business Layer & REST

## Features (Phase 2)

- Logic for business leveraging the service layer
- Processing workflow for orders
- Mock enforcement of payment (ordering is not possible without payment)
- Order entity and repository added
- REST API expanded
- Tested with Postman

## Business Process (Phase 2)

- The user signs up
- The user makes a product listing
- A buyer browses merchandise
- The customer places an order
- Payment has been completed (mock)
- The order has been recorded as paid

## Endpoints (Phase 2)

- POST /orders
- GET /orders
- POST /orders/{id}/pay

---

# Phase 3 – User Interface & Functional Application

## Features (Phase 3)

- Browser-based User Interface added
- Static HTML pages created for Users, Products, and Orders
- JavaScript fetch() calls connect UI to REST APIs
- Input validation added in UI forms
- Order placement and payment flow available from UI
- Confirmation messages shown after order and payment
- Business rules still enforced in Service layer
- UI + REST tested together

## UI Pages

- index.html — Home page
- users.html — Manage users
- products.html — Manage products
- orders.html — Place and pay orders

---

# System Architecture (Final)

This project uses a **4-layer architecture**:

- Entity Layer — Data Models
- Repository Layer — Database access
- Service Layer — Business Logic
- Controller Layer — REST API endpoints

Uses Spring Boot + JPA + H2 in-memory database.

---

# Main Entities

The system includes interconnected entities:

- UserAccount
- Product
- Order
- Payment
- Category
- OrderItem
- Review

These demonstrate one-to-many and many-to-one relationships.

---

# How To Run

## Requirements

- Java 17 or higher
- Maven
- IntelliJ IDEA
- Visual Studio

## Run

Run the main class:

CampusMarketplaceApplication.java

or:

mvn spring-boot:run

---

## Open in Browser

http://localhost:8080

---

## H2 Console

http://localhost:8080/h2-console

JDBC URL:

jdbc:h2:mem:campusdb

---

# Postman Testing (Phase 3)

Test these endpoints:

Users:
- POST /users
- GET /users

Products:
- POST /products
- GET /products

Orders:
- POST /orders
- GET /orders
- POST /orders/{id}/pay

Payments:
- POST /payments
- GET /payments

---

# Reflection

Doing this project in phase 3 provided the opportunity to build a complete enterprise-style application using a 4-layer architecture: Entity, Repository, Service, and Controller layers. The backend REST API was successfully connected to a working browser-based user interface where users can register, add products, place orders, and perform mock payments.

Business rules were implemented in the service layer instead of controllers, which improved maintainability and separation of concerns. Multiple interconnected entities and relationships strengthened understanding of ORM and database design.

During development, several integration issues between UI and REST endpoints were debugged using Postman and browser developer tools. Across the phases, the system was improved by adding services, business logic, validation, UI pages, and confirmation feedback messages. Combined Postman and UI testing confirmed correct business flow behavior.
