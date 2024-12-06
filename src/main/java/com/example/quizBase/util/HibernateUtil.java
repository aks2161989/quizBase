package com.example.quizBase.util;

import com.example.quizBase.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateUtil {

  /*  private static final SessionFactory sessionFactory;

    static{
        try {
            sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
        } catch (Throwable ex){
            throw new ExceptionInInitializerError(ex);
        }
    }*/

    @Bean
    public static SessionFactory sessionFactory(){

        Map<String, Object> settings = new HashMap<>();

        settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/quizbase?createDatabaseIfNotExist=true");
        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "12345678");

        settings.put("hibernate.hbm2ddl.auto", "update");
        settings.put("spring.jpa.hibernate.ddl-auto", "none");
        settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        settings.put("hibernate.dialect.storage_engine", "innodb");
       // settings.put("hibernate.show-sql", "true");;
        //settings.put("spring.datasource.initialization-mode", "always");
        settings.put("spring.sql.init.mode", "always");
        settings.put("spring.sql.init.schema-locations", "classpath:import.sql");
        settings.put("spring.sql.init.data-locations", "classpath:import.sql");
        settings.put("hibernate.current_session_context_class", "thread");

        //settings.put("hibernate.connection.autocommit", "true");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();
        MetadataSources metaDataSources = new MetadataSources(serviceRegistry);
        metaDataSources.addPackage("com.example.quizBase.models");
        metaDataSources.addAnnotatedClass(User.class);
        metaDataSources.addAnnotatedClass(Category.class);
        metaDataSources.addAnnotatedClass(Quiz.class);
        metaDataSources.addAnnotatedClass(Result.class);
        metaDataSources.addAnnotatedClass(Response.class);
        metaDataSources.addAnnotatedClass(Option.class);
        metaDataSources.addAnnotatedClass(Question.class);
        Metadata metaData = metaDataSources.buildMetadata();

        return metaData.getSessionFactoryBuilder().build();
    }

    public static void shutdown(){
        sessionFactory().close();
    }
}
