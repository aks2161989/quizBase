package com.example.quizBase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class QuizBaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizBaseApplication.class, args);
    }
}
