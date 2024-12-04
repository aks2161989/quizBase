package com.example.quizBase.dao;

import com.example.quizBase.models.User;
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
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User findById(long id){
        try(Session session = sessionFactory.openSession()){
            return session.get(User.class, id);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return null;
        }
    }
    public Optional<User> findByUsername(String username){

        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("from User where username = :username", User.class).setParameter("username", username).uniqueResult();
            return Optional.ofNullable(user);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Optional.empty();
        }
    }
    public Optional<User> findByEmail(String email){

        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("from User where email = :email", User.class).setParameter("email", email).uniqueResult();
            return Optional.ofNullable(user);
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Optional.empty();
        }
    }
    public boolean existsByUsername(String username){
        return findByUsername(username).isPresent();
    }
    public boolean existsByEmail(String email){
        return findByEmail(email).isPresent();
    }

    public List<User> findAllUsers(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from User", User.class).list();
        }
        catch (Exception e){
            e.printStackTrace(err);
            return Collections.emptyList();
        }
    }
    public void save(User user){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.saveOrUpdate(user);
            transaction.commit();
        }catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
