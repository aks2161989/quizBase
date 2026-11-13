package com.example.quizBase.dto;

public class QuizResultDto {
    private Long quizId;
    private String quizTitle;
    private int score;
    private int total;
    private int correctCount;
    private int incorrectCount;
    private int unansweredCount;
    private double percentage;
    private boolean passed;

    public QuizResultDto() {
    }

    public QuizResultDto(Long quizId, String quizTitle, int score, int total,
                         int correctCount, int incorrectCount, int unansweredCount,
                         double percentage, boolean passed) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.total = total;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
        this.percentage = percentage;
        this.passed = passed;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(int unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
