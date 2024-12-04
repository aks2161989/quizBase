package com.example.quizBase.dao;

import com.example.quizBase.models.Question;
import com.example.quizBase.models.Quiz;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
    public List<Question> findByQuiz(Long quizId){
        if (quizId == null) {
            err.println("Quiz ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Question where quiz.id = :quizId";
            Query<Question> query = session.createQuery(queryString, Question.class);
            query.setParameter("quizId", quizId);
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
    public void save(Question question){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.saveOrUpdate(question);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
