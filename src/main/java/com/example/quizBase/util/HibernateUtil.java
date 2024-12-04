package com.example.quizBase.util;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static{
        try {
            sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
        } catch (Throwable ex){
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Bean
    public static SessionFactory sessionFactory(){
        return sessionFactory;
    }

    public static void shutdown(){
        sessionFactory().close();
    }
}
