package dao;

import models.Question;
import models.Quiz;
import models.Result;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static java.lang.System.err;

@Repository
public class ResultRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ResultRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Result> findByUser(User user){
        if (user == null) {
            err.println("User is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where user = :user";
            Query<Result> query = session.createQuery(queryString, Result.class);
            query.setParameter("user", user);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Result> findByQuiz(Quiz quiz){
        if (quiz == null) {
            err.println("Quiz is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where quiz = :quiz";
            Query<Result> query = session.createQuery(queryString, Result.class);
            query.setParameter("quiz", quiz);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Result> findTop10ByQuizOrderByScoreDesc(Quiz quiz){
        if (quiz == null) {
            err.println("Quiz is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where quiz = :quiz order by score desc";
            Query<Result> query = session.createQuery(queryString, Result.class)
                .setParameter("quiz", quiz)
                .setMaxResults(10);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
}
