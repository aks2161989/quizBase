package com.example.quizBase.dao;

import com.example.quizBase.models.Option;
import com.example.quizBase.models.Question;
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
public class OptionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public OptionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Option findById(Long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Option.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public List<Option> findByQuestion(Question question){
        if (question == null) {
            err.println("Quiz is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Option where question = :question";
            Query<Option> query = session.createQuery(queryString, Option.class);
            query.setParameter("question", question);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Option> findByQuestion(Long questionId){
        if (questionId == null) {
            err.println("Quiz ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Option where question.id = :questionId";
            Query<Option> query = session.createQuery(queryString, Option.class);
            query.setParameter("questionId", questionId);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public void save(Option option){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.saveOrUpdate(option);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
