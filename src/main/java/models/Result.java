package models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int score;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date attempted_date;

    public Result() {
    }

    public Result(long id, User user, Quiz quiz, int score, Date attempted_date) {
        this.id = id;
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.attempted_date = attempted_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getAttempted_date() {
        return attempted_date;
    }

    public void setAttempted_date(Date attempted_date) {
        this.attempted_date = attempted_date;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", user=" + user +
                ", quiz=" + quiz +
                ", score=" + score +
                ", attempted_date=" + attempted_date +
                '}';
    }
}
