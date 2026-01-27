package org.example.aggrevateddata;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base test class that provides PostgreSQL Testcontainers support.
 * All integration tests should extend this class to use a real PostgreSQL database.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}
