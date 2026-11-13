package com.example.quizBase.dto;

import java.util.HashMap;
import java.util.Map;

public class QuizSubmissionDto {
    private Map<Long, Long> answers = new HashMap<>();

    public QuizSubmissionDto() {
    }

    public QuizSubmissionDto(Map<Long, Long> answers) {
        this.answers = answers != null ? answers : new HashMap<>();
    }

    public Map<Long, Long> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, Long> answers) {
        this.answers = answers != null ? answers : new HashMap<>();
    }
}
