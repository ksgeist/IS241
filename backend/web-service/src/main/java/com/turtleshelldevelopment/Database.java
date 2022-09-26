package com.turtleshelldevelopment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final HikariConfig config = new HikariConfig();
    static HikariDataSource db;


    public Database() {
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(300000);
        config.setConnectionTimeout(120000);
        config.setLeakDetectionThreshold(300000);
        config.setJdbcUrl("jdbc:mariadb://" + WebServer.env.get("DB_URL") + "/is241_mo_vat");
        config.setUsername(WebServer.env.get("DB_USERNAME", "db_team"));
        config.setPassword(WebServer.env.get("DB_PASSWORD", ""));
        db = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

}
