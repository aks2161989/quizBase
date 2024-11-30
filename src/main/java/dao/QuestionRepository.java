package dao;

import models.Question;
import models.Quiz;
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
public class QuestionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public QuestionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Question findById(long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Question.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public List<Question> findByQuiz(Quiz quiz){
        if (quiz == null) {
            err.println("Quiz is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Question where quiz = :quiz";
            Query<Question> query = session.createQuery(queryString, Question.class);
            query.setParameter("quiz", quiz);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Question> findAllQuestions(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from Question", Question.class).list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
}
