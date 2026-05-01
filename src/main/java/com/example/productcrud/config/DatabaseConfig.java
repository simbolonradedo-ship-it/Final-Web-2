package com.example.productcrud.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Value;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    @Value("${PGHOST:localhost}")
    private String dbHost;

    @Value("${PGUSER:postgres}")
    private String dbUser;

    @Value("${PGPASSWORD:password}")
    private String dbPassword;

    @Value("${PGDATABASE:postgres}")
    private String dbName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String url = "jdbc:postgresql://" + dbHost + ":5432/" + dbName;
        dataSource.setUrl(url);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
