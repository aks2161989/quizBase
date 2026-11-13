package com.example.quizBase.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizCreateRequest {
    private String title;
    private String description;
    private Long categoryId;
    private List<QuestionItem> questions = new ArrayList<>();

    public static class QuestionItem {
        private Long id;
        private String text;
        private List<OptionItem> options = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<OptionItem> getOptions() {
            return options;
        }

        public void setOptions(List<OptionItem> options) {
            this.options = options != null ? options : new ArrayList<>();
        }
    }

    public static class OptionItem {
        private Long id;
        private String text;
        private Boolean isCorrect;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Boolean getIsCorrect() {
            return isCorrect != null && isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<QuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionItem> questions) {
        this.questions = questions != null ? questions : new ArrayList<>();
    }
}
