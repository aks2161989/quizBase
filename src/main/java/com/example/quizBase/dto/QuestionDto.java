package com.example.quizBase.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionDto {
    private Long id;
    private String text;
    private List<OptionDto> options = new ArrayList<>();

    public QuestionDto() {
    }

    public QuestionDto(Long id, String text, List<OptionDto> options) {
        this.id = id;
        this.text = text;
        this.options = options != null ? options : new ArrayList<>();
    }

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

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
