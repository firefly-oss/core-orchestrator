# Core Orchestrator

A workflow orchestration service built with Spring Boot and Camunda 8 (Zeebe) for managing customer-related business processes.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
- [API Documentation](#api-documentation)
- [BPMN Processes](#bpmn-processes)
- [Configuration](#configuration)
- [Development](#development)

## Overview

Core Orchestrator is a microservice that orchestrates business processes related to customer management. It uses Camunda 8 (Zeebe) as the workflow engine to execute BPMN processes. The service provides REST APIs for starting processes and implements workers that execute the tasks defined in those processes.

Key features:
- Orchestration of customer creation processes (legal and natural persons)
- Integration with external services through adapters
- Asynchronous processing using Zeebe workflow engine
- RESTful API for process initiation

## Architecture

The Core Orchestrator follows a microservice architecture pattern and is built using the following components:

### Components

1. **REST Controllers**: Expose APIs for starting business processes
2. **Process Deployer**: Deploys BPMN process definitions to the Zeebe engine
3. **Job Workers**: Execute tasks defined in the BPMN processes
4. **Service Adapters**: Integrate with external services

### Technology Stack

- **Java 21**: Programming language
- **Spring Boot**: Application framework
- **Spring WebFlux**: Reactive web framework
- **Camunda 8 (Zeebe)**: Workflow engine
- **Maven**: Dependency management and build tool

### Flow Diagram

```
Client → REST API → Zeebe Workflow Engine → Job Workers → External Services
```

## Setup and Installation

### Prerequisites

- Java 21
- Maven
- Zeebe broker (Camunda 8 Platform)

### Building the Application

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file directly:

```bash
java -jar target/core-orchestrator-0.0.1-SNAPSHOT.jar
```

## API Documentation

The Core Orchestrator exposes the following REST endpoints:

### Create Legal Person

Creates a new legal person (business entity) by starting a BPMN process.

- **URL**: `/api/v1/customers/create-legal-person`
- **Method**: `POST`
- **Request Body**: JSON object with legal person details (FrontLegalPersonDTO)
- **Response**: JSON object with process instance key and status
- **Example Request**:
  ```json
  {
    "legalName": "Example Corp",
    "registrationNumber": "123456789",
    "address": {
      "street": "123 Main St",
      "city": "Exampleville",
      "postalCode": "12345",
      "country": "US"
    }
  }
  ```

- **Example Response**:
  ```json
  {
    "processInstanceKey": 2251799813685249,
    "status": "started"
  }
  ```

### Create Natural Person

Creates a new natural person (individual) by starting a BPMN process.

- **URL**: `/api/v1/customers/create-natural-person`
- **Method**: `POST`
- **Request Body**: JSON object with natural person details (FrontNaturalPersonDTO)
- **Response**: JSON object with process instance key and status
- **Example Request**:
  ```json
  {
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "address": {
      "street": "123 Main St",
      "city": "Exampleville",
      "postalCode": "12345",
      "country": "US"
    }
  }
  ```

- **Example Response**:
  ```json
  {
    "processInstanceKey": 2251799813685250,
    "status": "started"
  }
  ```

## BPMN Processes

The Core Orchestrator deploys and executes the following BPMN processes:

### Create Legal Person Process

A process for creating a legal person (business entity) with the following steps:
1. Start event: "User Creation Requested"
2. Service task: "Get User Information" (calls external service)
3. Service task: "Store Legal Person Data" (stores data locally)
4. End event: "User Created"

### Create Natural Person Process

A process for creating a natural person (individual) with the following steps:
1. Start event: "Natural Person Creation Requested"
2. Service task: "Get Natural Person Information" (calls external service)
3. End event: "Natural Person Created"

## Configuration

The application can be configured using the following properties in `application.properties`:

```properties
# Application configuration
spring.application.name=camunda-orchestrator
server.port=8081

# Zeebe configuration
camunda.client.zeebe.base-url=http://localhost:26500

# Worker configuration
camunda.client.zeebe.defaults.name=user-info-worker

# Logging configuration
logging.level.com.example.orchestrator=INFO
```

### Configuration Options

| Property | Description | Default Value |
|----------|-------------|---------------|
| `spring.application.name` | Application name | camunda-orchestrator |
| `server.port` | Server port | 8081 |
| `camunda.client.zeebe.base-url` | Zeebe broker URL | http://localhost:26500 |
| `camunda.client.zeebe.defaults.name` | Default worker name | user-info-worker |
| `logging.level.com.example.orchestrator` | Logging level | INFO |

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── core/
│   │           └── orchestrator/
│   │               ├── config/
│   │               │   └── ProcessDeployer.java
│   │               ├── controller/
│   │               │   └── CustomerController.java
│   │               ├── worker/
│   │               │   └── CustomerWorker.java
│   │               └── CoreOrchestratorApplication.java
│   └── resources/
│       ├── bpmn/
│       │   ├── create-legal-person-process.bpmn
│       │   └── create-natural-person-process.bpmn
│       └── application.properties
```

### Adding a New Process

1. Create a new BPMN file in `src/main/resources/bpmn/`
2. Add the process deployment to `ProcessDeployer.java`
3. Create a controller endpoint to start the process
4. Implement job workers for the process tasks
