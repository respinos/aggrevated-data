package org.example.aggrevateddata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrentPossibleObjectsTests extends AbstractIntegrationTest {
    @Autowired
    PossibleObjectMapper possibleObjectMapper;

    @Autowired
    ObjectFileMapper objectFileMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        jdbcTemplate.execute("TRUNCATE TABLE object_files CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE possible_objects RESTART IDENTITY CASCADE");

        var possibleObject = new PossibleObject(
                null,
                null,
                "root/1",
                "monograph",
                1,
                "bucket/1"
        );
        possibleObjectMapper.insert(possibleObject);

        var nextPossibleObject = new PossibleObject(
                null,
                null,
                "root/1",
                "monograph",
                2,
                "bucket/1"
        );
        possibleObjectMapper.insert(nextPossibleObject);
    }

    @Test
    public void testFindAllCurrentRoots() {
        var possibleObjects = possibleObjectMapper.findAllCurrentRoots(10, 0);
        for(PossibleObject obj : possibleObjects) {
            System.out.println(obj.getId() + " : " + obj.getIdentifier());
        }
        assertThat(possibleObjects.size()).isEqualTo(1);
    }

    @Test
    public void testFindByIdentifier() {
        var possibleObject = possibleObjectMapper.findCurrentByIdentifier("root/1");
        assertThat(possibleObject).isNotNull();
        assertThat(possibleObject.getVersionNumber()).isEqualTo(2);
    }

}
