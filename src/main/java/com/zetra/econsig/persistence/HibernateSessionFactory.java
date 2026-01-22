package com.zetra.econsig.persistence;

import java.sql.Connection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

@Component
public class HibernateSessionFactory {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    public final Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public final Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
}
