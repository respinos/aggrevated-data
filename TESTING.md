# Testing with Testcontainers

This project uses Testcontainers to run integration tests against a real PostgreSQL database instead of an in-memory H2 database. This ensures that PostgreSQL-specific features (like views, advanced SQL syntax, etc.) work correctly.

## Prerequisites

To run tests, you need:

1. **Docker** installed and running on your machine
   - Download from: https://www.docker.com/products/docker-desktop
   - Make sure Docker daemon is running before executing tests

2. **Java 21** (already configured in the project)

## Running Tests

### Run all tests:
```bash
./gradlew test
```

### Run a specific test:
```bash
./gradlew test --tests PostgreSQLViewTest
```

### Run tests with verbose output:
```bash
./gradlew test --info
```

## How It Works

### Testcontainers Configuration

The project uses Spring Boot's Testcontainers support:

1. **TestcontainersConfiguration.java** - Defines a PostgreSQL container bean that Spring Boot automatically uses for datasource configuration
2. **AbstractIntegrationTest.java** - Base class that all integration tests extend to get PostgreSQL support
3. **application.properties** (test) - Simplified configuration that lets Testcontainers handle the datasource

### What Gets Tested

- **PostgreSQL Views**: The `current_possible_objects` view works correctly (unlike H2)
- **Column Mapping**: Underscore to camelCase mapping (e.g., `bin_identifier` → `binIdentifier`)
- **MyBatis Mappers**: All CRUD operations with the real database
- **Lazy Loading**: Recursive lazy loading of child objects

### Container Lifecycle

- Testcontainers automatically starts a PostgreSQL container before tests
- The container is shared across all tests in the same test run (singleton)
- The container is automatically stopped after all tests complete
- Schema is initialized using `schema.sql` before each test context

## Troubleshooting

### "Could not find a valid Docker environment"

Make sure Docker Desktop is running. You can verify with:
```bash
docker ps
```

### Tests are slow

First test run downloads the PostgreSQL Docker image (~80MB). Subsequent runs are much faster as the image is cached.

### Port conflicts

If port 5432 is already in use, Testcontainers will automatically use a different random port. No configuration needed.

## Advantages Over H2

1. **True PostgreSQL compatibility** - Views, CTEs, window functions all work correctly
2. **Same database in dev and test** - No surprises when deploying
3. **Better debugging** - Can connect to the container and inspect data during test execution
4. **Real constraints** - Foreign keys, check constraints behave exactly as in production

## Migration from H2

The following changes were made to migrate from H2:

1. Removed `com.h2database:h2` dependency
2. Added Testcontainers dependencies:
   - `org.springframework.boot:spring-boot-testcontainers`
   - `org.testcontainers:junit-jupiter`
   - `org.testcontainers:postgresql`
3. Created `TestcontainersConfiguration` with PostgreSQL container
4. Updated test classes to extend `AbstractIntegrationTest`
5. Simplified test `application.properties` to use Testcontainers datasource
