# Game Management System

This is a microservices-based application designed to manage game-related data and user actions. The application is built using Spring Boot for the backend, Docker for containerization, and Spring Cloud Gateway as the API gateway. The system consists of multiple microservices, including authentication, user management, game management, and more.

## Architecture

The application is composed of the following microservices:

1. **Spring Cloud Gateway**: 
   - Acts as the API gateway, routing requests to the appropriate microservices.
   - Routes requests for game-related data, user actions, and authentication verification.

2. **Authentication Service**: 
   - Manages user authentication and authorization.
   - Handles JWT-based authentication.

3. **User Management Service**: 
   - Handles user-specific data, such as the games liked by the user, top-rated games by the user, etc.

4. **Game Management Service**: 
   - Manages game-related data such as game details, genres, ratings, etc.
   - Interacts with the PostgreSQL database for persistent storage.

5. **PostgreSQL Database**: 
   - Stores persistent data related to games, users, and their interactions.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Docker**: For containerization and running the services in isolated containers.
- **Docker Compose**: For orchestrating the multi-container Docker setup.

## Getting Started

Follow these steps to get the application up and running:

1. Clone this repository to your local machine:

   ```bash
   git clone <repository-url>
   cd <project-directory>
Build the Docker images for each service:

The docker-compose.yml file is set up to automatically build the required images for all services when you start the containers. Make sure the Dockerfile for each service is correctly defined in their respective directories.

Start the services using Docker Compose:

Run the following command to start all the services (PostgreSQL, Gateway, Authentication, User Management, and Game Management) in containers:


docker-compose up --build
This will:

Start the PostgreSQL container.
Build and start the microservices containers.
The Spring Cloud Gateway will be available at http://localhost:8080.
Verify the services are running:

You can check if the services are running properly by visiting the following endpoints:

Spring Cloud Gateway: http://localhost:8080
Game Management Service: http://localhost:8081
User Management Service: http://localhost:8082
Authentication Service: http://localhost:8083
PostgreSQL Database: localhost:5432
You should see the respective service responses.

API Endpoints
Here are the key API endpoints available in the application:

1. Authentication Service
POST /auth/register - Register a new user.
POST /auth/login - Login a user and get a JWT token.
2. Game Management Service
GET /games - Get a list of all available games.
GET /games/{id} - Get details of a specific game.
3. User Management Service
GET /user-data - Get data related to the user (e.g., liked games, top-rated games).
POST /user-action - Perform an action on a game (e.g., like or rate a game).
Spring Cloud Gateway Routes
The gateway handles routing requests to the appropriate microservices based on the request path:

/games/ -> Routed to Game Management Service.
/user-data/ -> Routed to User Management Service.
/user-action/ -> Routed to User Management Service.
/auth/ -> Routed to Authentication Service.
Docker Compose Configuration
The docker-compose.yml file defines the services and their respective configurations. It also sets up the following dependencies:

postgres: The PostgreSQL container for persistent data.
game-management: The service for managing games.
user-management: The service for handling user-related actions.
authentication: The service responsible for user authentication.
gateway: The Spring Cloud Gateway that routes traffic to the appropriate microservices.



Development
Running the Services Individually
If you want to run the services individually without Docker, you can follow these steps:

For each microservice, navigate to the respective directory and run:

mvn spring-boot:run
Make sure each service is running on its designated port (e.g., 8081 for Game Management, 8082 for User Management, 8083 for Authentication).

API Testing
You can use tools like Postman or curl to test the APIs. Here are some example curl commands for testing:

Register a new user:


curl -X POST http://localhost:8083/auth/register -d '{"username": "test", "password": "password"}' -H "Content-Type: application/json"
Login with a user:

curl -X POST http://localhost:8083/auth/login -d '{"username": "test", "password": "password"}' -H "Content-Type: application/json"
Fetch games:


curl http://localhost:8080/games
Troubleshooting
Connection Refused Error: Ensure that the services are running properly and that the Docker containers are correctly configured.
Service Dependency Issues: Check the logs of individual services (docker logs <container-name>) to diagnose potential issues.
Database Connection: If the application is unable to connect to PostgreSQL, make sure the postgres service is running and the database credentials in the environment variables are correct.



*Further Changes and improvments*: Introduce eureka server to change a lot of the hardcoding that is done. Add a notification service to send push notification. Finally get my lazy self to write frontend code.
