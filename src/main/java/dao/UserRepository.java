package dao;

import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

}
