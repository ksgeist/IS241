package com.turtleshelldevelopment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final HikariConfig config = new HikariConfig();
    static HikariDataSource db;


    public Database(String url, String username, String password) {
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(300000);
        config.setConnectionTimeout(120000);
        config.setLeakDetectionThreshold(300000);
        config.setJdbcUrl("jdbc:mariadb://" + url + "/is241_mo_vat");
        config.setUsername(username);
        config.setPassword(password);
        db = new HikariDataSource(config);
    }

    public Database() {
        this(WebServer.env.get("DB_URL"), WebServer.env.get("DB_USERNAME", "db_team"), WebServer.env.get("DB_PASSWORD", ""));
    }

    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

}
