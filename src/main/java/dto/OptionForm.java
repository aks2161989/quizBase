package dto;

import jakarta.validation.constraints.NotEmpty;

public class OptionForm {

    @NotEmpty(message = "Option text is required")
    private String text;

    private boolean isCorrect;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
