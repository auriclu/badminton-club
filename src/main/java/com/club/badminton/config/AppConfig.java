package com.club.badminton.config;

import com.club.badminton.dao.EventDao;
import com.club.badminton.dao.EventRegistrationDao;
import com.club.badminton.dao.UserDao;
import com.club.badminton.service.ApprovalStrategyFactory;
import com.club.badminton.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Root configuration class
 */
@Configuration
@ComponentScan(basePackages = "com.club.badminton")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${db.url}") String dbUrl;
    @Value("${db.username}") String dbUsername;
    @Value("${db.password}") String dbPassword;
    @Value("${db.driver}") String dbDriver;
    @Value("${db.poolSize}") int dbPoolSize;

    // Initialize the custom connection pool
    @Bean(destroyMethod = "shutdown")
    public CustomConnectionPool customConnectionPool() throws Exception {
        return new CustomConnectionPool(dbUrl, dbUsername, dbPassword, dbDriver, dbPoolSize);
    }

    // DAO Beans
    @Bean
    public UserDao userDao(CustomConnectionPool pool) { return new UserDao(pool); }

    @Bean
    public EventDao eventDao(CustomConnectionPool pool) { return new EventDao(pool); }

    @Bean
    public EventRegistrationDao eventRegistrationDao(CustomConnectionPool pool) { return new EventRegistrationDao(pool); }

    // Initialize factory for the logic of event approvals
    @Bean
    public ApprovalStrategyFactory approvalStrategyFactory(EventDao eventDao) {
        return new ApprovalStrategyFactory(eventDao);
    }

    // Wires the registration service with its DAOs and strategy factory
    @Bean
    public EventRegistrationService eventRegistrationService(EventRegistrationDao regDao, EventDao eventDao, ApprovalStrategyFactory factory) {
        return new EventRegistrationService(regDao, eventDao, factory);
    }
}