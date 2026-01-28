# Todo App

A simple Todo application built with Java Spring Boot and MongoDB.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete todos
- **Priority Levels**: Assign LOW, MEDIUM, or HIGH priority to todos
- **Completion Status**: Mark todos as completed or incomplete
- **Search Functionality**: Search todos by title
- **Filter Options**: Filter todos by status or priority
- **Timestamps**: Automatic tracking of creation and update times
- **REST API**: Fully featured REST API for all operations

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data MongoDB**
- **MongoDB**
- **Maven**
- **Docker & Docker Compose**

## Prerequisites

- Java 17 or higher
- Maven 3.9.6 or higher
- Docker & Docker Compose (for containerized setup)
- MongoDB (if running without Docker)

## Getting Started

### Option 1: Local Development

1. **Clone or navigate to the project directory**
   ```bash
   cd todo-app
   ```

2. **Install dependencies and build**
   ```bash
   mvn clean install
   ```

3. **Run MongoDB locally** (or update `application.properties` with your MongoDB connection)
   ```bash
   mongod
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Option 2: Docker & Docker Compose (Recommended)

1. **Navigate to the project directory**
   ```bash
   cd todo-app
   ```

2. **Start the application with Docker Compose**
   ```bash
   docker-compose up --build
   ```

   This will:
   - Build the Docker image for the Todo app
   - Start a MongoDB container
   - Start the Todo app container
   - Both services will be connected through a Docker network

3. **Access the application**
   - API: `http://localhost:8080`
   - MongoDB: `localhost:27017`

4. **Stop the application**
   ```bash
   docker-compose down
   ```

   To also remove volumes:
   ```bash
   docker-compose down -v
   ```

## API Endpoints

### Get all todos
```
GET /api/todos
```

### Get todo by ID
```
GET /api/todos/{id}
```

### Create a new todo
```
POST /api/todos
Content-Type: application/json

{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "priority": "HIGH"
}
```

### Update a todo
```
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "MEDIUM",
  "completed": false
}
```

### Delete a todo
```
DELETE /api/todos/{id}
```

### Get todos by completion status
```
GET /api/todos/status/{status}
```
- Replace `{status}` with `true` or `false`

### Get todos by priority
```
GET /api/todos/priority/{priority}
```
- Replace `{priority}` with `LOW`, `MEDIUM`, or `HIGH`

### Search todos by title
```
GET /api/todos/search/{title}
```

### Mark todo as completed
```
PUT /api/todos/{id}/complete
```

### Mark todo as incomplete
```
PUT /api/todos/{id}/incomplete
```

### Health check
```
GET /api/todos/health
```

## Example Usage with cURL

```bash
# Create a new todo
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Complete Spring Boot tutorial",
    "priority": "HIGH"
  }'

# Get all todos
curl http://localhost:8080/api/todos

# Mark as completed
curl -X PUT http://localhost:8080/api/todos/{id}/complete

# Delete a todo
curl -X DELETE http://localhost:8080/api/todos/{id}
```

## Project Structure

```
todo-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/todoapp/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── model/          # Entity models
│   │   │   ├── repository/     # MongoDB repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── TodoAppApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Configuration

### application.properties

Key configuration options:

```properties
spring.application.name=todo-app
server.port=8080
spring.data.mongodb.uri=mongodb://localhost:27017/todo_db
spring.data.mongodb.database=todo_db
```

For Docker Compose, MongoDB URI is configured with authentication:
```properties
spring.data.mongodb.uri=mongodb://admin:password@mongo:27017/todo_db?authSource=admin
```

## Building Docker Image Manually

If you want to build the Docker image without Docker Compose:

```bash
docker build -t todo-app:1.0.0 .
```

Run the container:
```bash
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/todo_db \
  --network your-network \
  todo-app:1.0.0
```

## Troubleshooting

### Connection refused error when connecting to MongoDB
- Ensure MongoDB is running on the correct host and port
- Check the `spring.data.mongodb.uri` in `application.properties`
- For Docker Compose, ensure both services are on the same network

### Port already in use
- Change the port in `application.properties` or docker-compose.yml
- Or kill the process using the port:
  ```bash
  # macOS/Linux
  lsof -i :8080
  kill -9 <PID>
  
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

### Build issues
- Ensure Java 17+ is installed: `java --version`
- Clear Maven cache: `mvn clean`
- Rebuild: `mvn clean package`

## Development

To enable hot-reload during development, use:
```bash
mvn spring-boot:run
```

Or with IntelliJ IDEA, use the built-in Run/Debug configuration.

## License

MIT License
