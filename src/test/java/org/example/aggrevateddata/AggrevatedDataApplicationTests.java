package org.example.aggrevateddata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = MyBatisConfig.class)
class AggrevatedDataApplicationTests {

    @Autowired
    PossibleObjectMapper possibleObjectMapper;

    @Autowired
    ObjectFileMapper objectFileMapper;

    @Test
    void contextLoads() {
    }



}
