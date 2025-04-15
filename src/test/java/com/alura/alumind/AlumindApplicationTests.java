package com.alura.alumind;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AlumindApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads successfully
    }
}