package com.example.quizBase.dto;

public class DashboardStatsDto {
    private int totalQuizzes;
    private int myQuizzesCount;
    private int totalCategories;
    private String username;
    private String role;
    private String fullName;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(int totalQuizzes, int myQuizzesCount, int totalCategories,
                             String username, String role, String fullName) {
        this.totalQuizzes = totalQuizzes;
        this.myQuizzesCount = myQuizzesCount;
        this.totalCategories = totalCategories;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(int totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public int getMyQuizzesCount() {
        return myQuizzesCount;
    }

    public void setMyQuizzesCount(int myQuizzesCount) {
        this.myQuizzesCount = myQuizzesCount;
    }

    public int getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(int totalCategories) {
        this.totalCategories = totalCategories;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
