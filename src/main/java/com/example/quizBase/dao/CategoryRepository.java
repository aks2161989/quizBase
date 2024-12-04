package com.example.quizBase.dao;

import com.example.quizBase.models.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static java.lang.System.err;

@Repository
public class CategoryRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CategoryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Category findById(long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(Category.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public Optional<Category> findByName(String name){
        try(Session session = sessionFactory.openSession()){
            Category category = session.createQuery("from Category where name = :name", Category.class).setParameter("name", name).uniqueResult();
            return Optional.ofNullable(category);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Optional.empty();
        }
    }
    public List<Category> findAllCategories(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from Category", Category.class).list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public void save(Category category){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.saveOrUpdate(category);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public void update(Category category){
        save(category);
    }
    public void deleteById(Long id){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            Category category = findById(id);
            session.remove(category);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
