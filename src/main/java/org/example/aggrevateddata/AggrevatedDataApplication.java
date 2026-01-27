package org.example.aggrevateddata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggrevatedDataApplication {

    private final PossibleObjectMapper possibleObjectMapper;
    private final ObjectFileMapper objectFileMapper;

    public AggrevatedDataApplication(PossibleObjectMapper possibleObjectMapper, ObjectFileMapper objectFileMapper) {
        this.possibleObjectMapper = possibleObjectMapper;
        this.objectFileMapper = objectFileMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(AggrevatedDataApplication.class, args);
    }

}
