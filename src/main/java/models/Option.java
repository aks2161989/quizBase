package models;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Boolean is_correct;

    public Option() {
    }

    public Option(long id, Question question, String text, Boolean is_correct) {
        this.id = id;
        this.question = question;
        this.text = text;
        this.is_correct = is_correct;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(Boolean is_correct) {
        this.is_correct = is_correct;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", question=" + question +
                ", text='" + text + '\'' +
                ", is_correct=" + is_correct +
                '}';
    }
}
