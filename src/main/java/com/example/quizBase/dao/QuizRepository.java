package com.example.quizBase.dao;

import com.example.quizBase.models.Category;
import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.User;
import com.example.quizBase.util.HibernateUtil;
import jakarta.persistence.EntityNotFoundException;
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
public class QuizRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public QuizRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Quiz findById(Long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Quiz.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public List<Quiz> findByCategory(Category category){
        if (category == null) {
            err.println("Category is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Quiz where category = :category";
            Query<Quiz> query = session.createQuery(queryString, Quiz.class);
            query.setParameter("category", category);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Quiz> findByCategory(Long categoryId){
        if (categoryId == null) {
            err.println("Category ID is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Quiz where category.id = :categoryId";
            Query<Quiz> query = session.createQuery(queryString, Quiz.class);
            query.setParameter("categoryId", categoryId);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Quiz> findByCreator(User creator){

        if (creator == null) {
            err.println("Creator is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Quiz where creator = :creator";
            Query<Quiz> query = session.createQuery(queryString, Quiz.class);
            query.setParameter("creator", creator);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Quiz> findByCreator(Long creatorId){

        if (creatorId == null) {
            err.println("Creator is null");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Quiz where creator.id = :creatorId";
            Query<Quiz> query = session.createQuery(queryString, Quiz.class);
            query.setParameter("creatorId", creatorId);
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Quiz> findByTitleContainingIgnoreCase(String title){
        if (title == null || title.trim().isEmpty()) {
            err.println("Title is null or empty");
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.openSession()){
            String queryString = "from Quiz where lower(title) like :title";
            Query<Quiz> query = session.createQuery(queryString, Quiz.class);
            query.setParameter("title", "%" +title.toLowerCase() + "%");
            return query.list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public List<Quiz> findAllQuizzes(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from Quiz", Quiz.class).list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    @Transactional
    public void save(Quiz quiz){
        Transaction transaction = null;
        try(Session session = HibernateUtil.sessionFactory().getCurrentSession()){
            transaction = session.beginTransaction();
            session.persist(quiz);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null && transaction.isActive()){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Transactional
    public void update(Quiz quiz){
        try(Session session = sessionFactory.getCurrentSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(quiz);
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to update quiz");
        }
    }
    public void deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Quiz quiz = findById(id);
            session.remove(quiz);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
