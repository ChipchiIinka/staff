package ru.egartech.staff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StaffApplicationTests {
    @Test
    void contextLoads() {
        // Этот тест проверяет, что контекст приложения загружается без ошибок.
    }
}
