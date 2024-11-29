package dao;

import models.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import static java.lang.System.err;

@Repository
public class CategoryRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CategoryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
}
