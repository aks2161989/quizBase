package dao;

import models.Option;
import models.Question;
import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

    public Option findById(long id){
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
}
