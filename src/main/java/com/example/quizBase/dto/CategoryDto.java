package com.example.quizBase.dto;

public class CategoryDto {
    private Long id;
    private String name;
    private String creatorUsername;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, String creatorUsername) {
        this.id = id;
        this.name = name;
        this.creatorUsername = creatorUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
}
