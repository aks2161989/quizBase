package models;

import jakarta.persistence.*;

@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option selectedOption;

    public Response() {
    }

    public Response(Result result, Question question, Option selectedOption) {
        this.result = result;
        this.question = question;
        this.selectedOption = selectedOption;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Option getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Option option) {
        this.selectedOption = option;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", result=" + result +
                ", question=" + question +
                ", option=" + selectedOption +
                '}';
    }
}
