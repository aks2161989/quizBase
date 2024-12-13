package dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class QuestionForm {

    @NotEmpty(message = "Question text is required")
    private String text;

    private List<OptionForm> options = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int correctOption;


    public List<OptionForm> getOptions() {
        return options;
    }

    public void setOptions(List<OptionForm> options) {
        this.options = options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }
    public void markCorrectOption() {
        for (int i = 0; i < options.size(); i++) {
            options.get(i).setCorrect(i == correctOption);
        }
    }
}
