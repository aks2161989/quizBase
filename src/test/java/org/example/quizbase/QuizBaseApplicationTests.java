package org.example.quizbase;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizBaseApplicationTests {

    @Test
    void testPasswordEncoderMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password123");
        assertTrue(encoder.matches("password123", hash));
    }
}
