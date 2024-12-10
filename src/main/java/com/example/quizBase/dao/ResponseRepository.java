package com.example.quizBase.dao;

import com.example.quizBase.models.Question;
import com.example.quizBase.models.Response;
import com.example.quizBase.models.Result;
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
public class ResponseRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public ResponseRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Response findById(Long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Response.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public List<Response> findByResult(Result result){
        if (result == null) {
            err.println("Result is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Response where result = :result";
            Query<Response> query = session.createQuery(queryString, Response.class);
            query.setParameter("result", result);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Response> findByResult(Long resultId){
        if (resultId == null) {
            err.println("Result ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Response where result.id = :resultId";
            Query<Response> query = session.createQuery(queryString, Response.class);
            query.setParameter("resultId", resultId);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }

    public List<Response> findByQuestion(Question question){
        if (question == null) {
            err.println("Question is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Response where question = :question";
            Query<Response> query = session.createQuery(queryString, Response.class);
            query.setParameter("question", question);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Response> findByQuestion(Long questionId){
        if (questionId == null) {
            err.println("Question ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Response where question.id = :questionId";
            Query<Response> query = session.createQuery(queryString, Response.class);
            query.setParameter("questionId", questionId);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    @Transactional
    public void save(Response response){
        try(Session session = sessionFactory.getCurrentSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
