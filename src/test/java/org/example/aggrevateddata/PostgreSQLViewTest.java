package org.example.aggrevateddata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that PostgreSQL-specific features like views work correctly with Testcontainers.
 */
class PostgreSQLViewTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PossibleObjectMapper possibleObjectMapper;

    @Test
    void testCurrentPossibleObjectsViewExists() {
        // Query the view to ensure it was created successfully
        String sql = "SELECT COUNT(*) FROM current_possible_objects";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        assertNotNull(count);
        assertTrue(count >= 0, "View should be queryable");
    }

    @Test
    void testViewReturnsLatestVersions() {
        // Insert test data with multiple versions
        jdbcTemplate.update(
            "INSERT INTO possible_objects (identifier, type, version_number, bin_identifier) VALUES (?, ?, ?, ?)",
            "test-obj-1", "type1", 1, "bin1"
        );
        jdbcTemplate.update(
            "INSERT INTO possible_objects (identifier, type, version_number, bin_identifier) VALUES (?, ?, ?, ?)",
            "test-obj-1", "type1", 2, "bin2"
        );
        jdbcTemplate.update(
            "INSERT INTO possible_objects (identifier, type, version_number, bin_identifier) VALUES (?, ?, ?, ?)",
            "test-obj-2", "type2", 1, "bin3"
        );

        // Query the view - should only return the latest version of each identifier
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            "SELECT * FROM current_possible_objects ORDER BY identifier"
        );

        assertEquals(2, results.size(), "View should return 2 distinct objects");

        // Verify test-obj-1 has version 2 (latest)
        Map<String, Object> obj1 = results.get(0);
        assertEquals("test-obj-1", obj1.get("identifier"));
        assertEquals(2, obj1.get("version_number"));
        assertEquals("bin2", obj1.get("bin_identifier"));

        // Verify test-obj-2 has version 1 (only version)
        Map<String, Object> obj2 = results.get(1);
        assertEquals("test-obj-2", obj2.get("identifier"));
        assertEquals(1, obj2.get("version_number"));
        assertEquals("bin3", obj2.get("bin_identifier"));
    }

    @Test
    void testMyBatisMapperWorks() {
        // Insert test data
        PossibleObject obj = new PossibleObject();
        obj.setIdentifier("mapper-test");
        obj.setType("test-type");
        obj.setVersionNumber(1);
        obj.setBinIdentifier("mapper-bin");

        int inserted = possibleObjectMapper.insert(obj);
        assertEquals(1, inserted, "Should insert one record");
        assertNotNull(obj.getId(), "ID should be generated");

        // Verify we can retrieve it
        PossibleObject retrieved = possibleObjectMapper.findById(obj.getId());
        assertNotNull(retrieved);
        assertEquals("mapper-test", retrieved.getIdentifier());
        assertEquals("mapper-bin", retrieved.getBinIdentifier(), "binIdentifier should be mapped from bin_identifier column");
    }
}
