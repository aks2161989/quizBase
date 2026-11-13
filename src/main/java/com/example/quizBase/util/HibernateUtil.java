package com.example.quizBase.util;

import com.example.quizBase.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@EnableTransactionManagement
@Configuration
public class HibernateUtil {

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/quizbase?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}")
    private String dbUrl;

    @Value("${spring.datasource.username:root}")
    private String dbUsername;

    @Value("${spring.datasource.password:12345678}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String dbDriver;

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Value("${spring.jpa.properties.hibernate.dialect:org.hibernate.dialect.MySQLDialect}")
    private String dialect;

    @Bean(name = "entityManagerFactory")
    public SessionFactory sessionFactory() {
        Map<String, Object> settings = new HashMap<>();

        settings.put("hibernate.connection.driver_class", dbDriver);
        settings.put("hibernate.connection.url", dbUrl);
        settings.put("hibernate.connection.username", dbUsername);
        settings.put("hibernate.connection.password", dbPassword);

        settings.put("hibernate.hbm2ddl.auto", ddlAuto);
        settings.put("hibernate.dialect", dialect);
        settings.put("hibernate.dialect.storage_engine", "innodb");
        settings.put("hibernate.current_session_context_class", "thread");
        settings.put("current_session_context_class", "thread");

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

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
