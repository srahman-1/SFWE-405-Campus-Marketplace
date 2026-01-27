# SFWE-405-Campus-Marketplace
SFWE 405 course project: E-commerce shopping platform (Campus Marketplace) implementing a 3-layer architecture with ORM, repositories, REST API, and business rules.

**Student Name:** Samiur Rahman  
**Course:** SFWE 405  
**Project:** E-commerce Campus Marketplace  

# Campus Marketplace – Phase 1

Spring Boot backend for a campus marketplace application.

## Features (Phase 1)
- User and Product data model
- JPA entities with H2 database
- Spring Data JPA repositories
- REST API with controllers
- Tested using Postman

## Endpoints
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

# Phase 2 – Business Layer & REST

## Features (Phase 2)
- Logic for business leveraging the service layer
- Processing workflow for orders
- Mock enforcement of payment (ordering is not possible without payment)
- Place an entity and repository order.
- REST API expansion
- Tested with Postman

 ## Business Process
 - The user signs up
 - The user makes a product listing
 -  A buyer peruses merchandise
 -  The customer places an order
 -  Payment has been completed (mock)
 -  The order has been recorded as paid
   
 ## Endpoints 
 - POST /orders
 - POST /orders/{id}/pay
 - GET  /orders



