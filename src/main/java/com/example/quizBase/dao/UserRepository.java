package com.example.quizBase.dao;

import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.User;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.System.err;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User findById(Long id){
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
    public User findByUsernameAndPassword(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
            Optional<User> optionalUser = findByUsername(username.toLowerCase());

            if (!optionalUser.isPresent()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            User user = optionalUser.get();
            if (!password.equals(user.getPassword())) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            return user;
    }

}
