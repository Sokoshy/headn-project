package com.bibliotheque.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/bibliotheque";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";
    
    private static HikariDataSource dataSource;
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC_URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            
            // Configuration du pool de connexions
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}