# CodeByZ Microservices Skeleton (API Gateway + Auth Service)

This is a working skeleton:
- api-gateway (Spring Cloud Gateway)
- auth-service (REST + PostgreSQL + JWT + OAuth2 Google + SendGrid + Swagger)

Requirements:
- Java 17, Maven
- PostgreSQL (or docker compose)
- Env vars: GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, SEND_GRID

Run postgres:
  docker compose up -d postgres

Run auth:
  cd auth-service
  mvn -q -DskipTests spring-boot:run
  Swagger: http://localhost:8081/swagger-ui

Run gateway:
  cd api-gateway
  mvn -q -DskipTests spring-boot:run
  Gateway: http://localhost:8080

Routes:
- /api/auth/** -> auth-service
- /api/users/** -> auth-service
- /swagger-ui/**, /v3/api-docs/** -> auth-service
