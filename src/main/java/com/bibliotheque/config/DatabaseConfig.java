package com.bibliotheque.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static Properties properties;
    
    static {
        try {
            loadProperties();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName("org.postgresql.Driver");
            
            // Configuration du pool de connexions
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minimumIdle", "2")));
            config.setConnectionTimeout(Integer.parseInt(properties.getProperty("db.pool.connectionTimeout", "30000")));
            config.setIdleTimeout(Integer.parseInt(properties.getProperty("db.pool.idleTimeout", "600000")));
            config.setMaxLifetime(Integer.parseInt(properties.getProperty("db.pool.maxLifetime", "1800000")));
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
    }
    
    private static void loadProperties() throws IOException {
        properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Fichier de configuration config.properties non trouvé");
            }
            properties.load(input);
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