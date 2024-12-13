package dto;

import com.example.quizBase.models.*;

import java.util.ArrayList;
import java.util.List;

public class QuizForm {

    private String title;

    private String description;

    private String categoryName;

    private List<QuestionForm> questions = new ArrayList<>();

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<QuestionForm> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionForm> questions) {
        this.questions = questions;
    }

    public Quiz toQuiz(User creator, Category category){
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCreator(creator);
        quiz.setDescription(description);
        quiz.setCategory(category);

        for (QuestionForm questionForm: questions){
            Question question = new Question();
            question.setText(questionForm.getText());
            question.setQuiz(quiz);

            for(OptionForm optionForm: questionForm.getOptions()){
                Option option = new Option();
                option.setText(optionForm.getText());
                option.setIs_correct(optionForm.isCorrect());
                option.setQuiz(quiz);
                option.setQuestion(question);
                question.getOptions().add(option);
            }
            quiz.getQuestions().add(question);
        }
        return quiz;
    }
}
