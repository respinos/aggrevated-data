package org.example.aggrevateddata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PossibleObjectsTests extends AbstractIntegrationTest {
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

        String testDigestHex = "BCBCBCBC";
        byte[] digest = null;
        if (testDigestHex != null && !testDigestHex.isBlank()) {
            int len = testDigestHex.length();
            digest = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                digest[i / 2] = (byte) ((Character.digit(testDigestHex.charAt(i), 16) << 4)
                        + Character.digit(testDigestHex.charAt(i+1), 16));
            }
        }

        var objectFile = new ObjectFile(
                null,
                "file1.txt",
                "text/plain",
                "function:test",
                1024,
                digest,
                possibleObject.getVersionNumber(),
                LocalDateTime.now(),
                possibleObject.getId(),
                0
        );
        objectFileMapper.insert(objectFile);

        var nextPossibleObject = new PossibleObject(
                null,
                null,
                "root/1",
                "monograph",
                2,
                "bucket/1"
        );
        possibleObjectMapper.insert(nextPossibleObject);
        var nextObjectFile1 = new ObjectFile(
                null,
                "file1.txt",
                "text/plain",
                "function:test",
                1024,
                digest,
                nextPossibleObject.getVersionNumber(),
                LocalDateTime.now(),
                nextPossibleObject.getId(),
                0
        );
        objectFileMapper.insert(nextObjectFile1);
        var nextObjectFile2 = new ObjectFile(
                null,
                "file2.txt",
                "text/plain",
                "function:test",
                1024,
                digest,
                nextPossibleObject.getVersionNumber(),
                LocalDateTime.now(),
                nextPossibleObject.getId(),
                1
        );
        objectFileMapper.insert(nextObjectFile2);
    }

    @Test
    public void testFindAllVersions() {
        var possibleObject = possibleObjectMapper.findCurrentByIdentifier("root/1");
        var allVersions = possibleObjectMapper.findVersionsByIdentifier(possibleObject.getIdentifier());
        assertThat(allVersions.size()).isEqualTo(2);
    }

}
