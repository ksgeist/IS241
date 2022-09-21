package com.turtleshelldevelopment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static HikariConfig config = new HikariConfig();
    static HikariDataSource db;


    private static String INSERT_USER = "";

    public Database() {
        config.setJdbcUrl("jdbc:mariadb://db.colink02dev.com:3306/is241_mo_vat");
        config.setUsername(WebServer.env.get("DB_USERNAME", "db_team"));
        config.setPassword(WebServer.env.get("DB_PASSWORD", ""));
        db = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

}
