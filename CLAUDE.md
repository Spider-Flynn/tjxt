# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based microservices educational platform called "TianJi" built with:
- **Spring Boot 2.7.2** with Java 11
- **Spring Cloud 2021.0.3** for microservices architecture
- **Spring Cloud Alibaba** for cloud-native features
- **MyBatis Plus** for database operations
- Distributed architecture with multiple independent services

## Architecture

The project follows a microservices architecture with these core modules:

### Core Services:
- **tj-gateway**: API gateway with authentication and routing
- **tj-auth**: Authentication and authorization service with JWT
- **tj-user**: User management service
- **tj-course**: Course and content management
- **tj-learning**: Learning progress and interaction management
- **tj-trade**: Order and payment processing
- **tj-exam**: Exam and question management
- **tj-media**: File and media storage
- **tj-search**: Search and recommendation services
- **tj-data**: Data analytics and dashboard
- **tj-remark**: Like and interaction records
- **tj-message**: Messaging and notifications
- **tj-pay**: Payment processing

### Common Modules:
- **tj-common**: Shared utilities and common classes
- **tj-api**: API client interfaces and DTOs

## Build and Development Commands

### Maven Commands:
```bash
# Build entire project
mvn clean compile

# Run tests for all modules
mvn test

# Package all services
mvn package

# Install to local repository
mvn install

# Build specific module
mvn -pl tj-user clean compile

# Run specific service
mvn -pl tj-user spring-boot:run
```

### Docker Commands:
```bash
# Build Docker image for a service
docker build -t service-name .

# Run service in Docker (using startup.sh)
./startup.sh -n service-name -d module-path -c container-name -p port
```

### Service-Specific Commands:
Each service can be run individually using:
```bash
cd tj-user
mvn spring-boot:run
```

## Configuration

- Configuration files are in `src/main/resources/bootstrap-*.yml`
- Profiles: `dev`, `local`, default
- Database: MySQL with MyBatis Plus
- Cache: Redis integration
- Message Queue: RabbitMQ for async processing

## Testing

- Unit tests use Spring Boot Test framework
- Test files are located in `src/test/java/`
- Run tests with: `mvn test` or `mvn -pl module-name test`

## Code Style & Patterns

- Uses Lombok for boilerplate reduction
- Standard Spring Boot project structure
- Controller-Service-Mapper architecture
- DTO pattern for API communication
- Custom exceptions and error handling
- Redis for caching and distributed locks