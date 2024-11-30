package dao;

import models.Category;
import models.Question;
import models.Quiz;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    public Quiz findById(long id){
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

}
