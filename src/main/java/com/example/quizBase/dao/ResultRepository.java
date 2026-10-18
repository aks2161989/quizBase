package com.example.quizBase.dao;

import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.Result;
import com.example.quizBase.models.User;
import jakarta.transaction.Transactional;
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
public class ResultRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ResultRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Result findById(Long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Result.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
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
    public List<Result> findByUser(Long userId){
        if (userId == null) {
            err.println("User ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where user.id = :userId";
            Query<Result> query = session.createQuery(queryString, Result.class);
            query.setParameter("userId", userId);
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
    public List<Result> findByQuiz(Long quizId){
        if (quizId == null) {
            err.println("Quiz ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where quiz.id = :quizId";
            Query<Result> query = session.createQuery(queryString, Result.class);
            query.setParameter("quizId", quizId);
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
    public List<Result> findTop10ByQuizOrderByScoreDesc(Long quizId){
        if (quizId == null) {
            err.println("Quiz ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Result where quiz.id = :quizId order by score desc";
            Query<Result> query = session.createQuery(queryString, Result.class)
                    .setParameter("quizId", quizId)
                    .setMaxResults(10);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    @Transactional
    public void save(Result result){
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(result);
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to save result", e);
        }
    }
}
