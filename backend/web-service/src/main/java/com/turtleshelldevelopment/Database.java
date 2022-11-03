package com.turtleshelldevelopment;

import com.turtleshelldevelopment.utils.TextQR;
import com.turtleshelldevelopment.utils.db.Account;
import com.turtleshelldevelopment.utils.mfa.MultiFactorResponse;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.apache.commons.lang.RandomStringUtils;

import java.sql.*;

public class Database {
    private static final HikariConfig config = new HikariConfig();
    static HikariDataSource db;


    public Database(String url, String username, String password) {
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(300000);
        config.setConnectionTimeout(120000);
        config.setLeakDetectionThreshold(300000);
        config.setJdbcUrl("jdbc:mariadb://" + url + "/is241_mo_vat");
        config.setUsername(username);
        config.setPassword(password);
        db = new HikariDataSource(config);

        //Checks if there is a user in the database which essentially verifies that there is someone in the database and
        // if not create a new superuser
        try {
            BackendServer.serverLogger.info("Checking Account...");
            Connection connection = db.getConnection();
            PreparedStatement checkForUser = connection.prepareStatement("SELECT user_id FROM User LIMIT 1;");
            ResultSet set = checkForUser.executeQuery();
            if(set.next()) {
                System.out.println("Found account: " + set.getInt("user_id") + " on database " + url);
            } else {
                BackendServer.serverLogger.info("Creating System Admin Account...");
                String adminPassword = RandomStringUtils.random(16, true, true);
                String systemAdminUsername = "admin";
                Account admin = new Account(systemAdminUsername, adminPassword);
                CallableStatement statement = db.getConnection().prepareCall("CALL ADD_USER(?,?,?,?,?,?,?,?,?)");
                statement.setString(1, systemAdminUsername);
                statement.setBytes(2, admin.getPasswordHash());
                statement.setBytes(3, admin.getPasswordSalt());
                MultiFactorResponse tfa = admin.generateTOTPMultiFactor();
                statement.setString(4, tfa.secret());
                statement.setString(5, "admin");
                statement.setString(6, "admin");
                statement.setInt(7, 1);
                statement.setInt(8, 1);
                statement.setString(9, "unconfigured@example.com");
                if(statement.executeUpdate() == 1) {
                    BackendServer.serverLogger.info("Two-Factor Authentication for Admin: ");
                    BackendServer.serverLogger.info("\n" + TextQR.getQrStringFromURI(tfa.qr_data()));
                    BackendServer.serverLogger.info("Password: " + adminPassword);
                    BackendServer.serverLogger.info("Remember this Information, This information will not show on next startup!");
                }

                statement.close();
            }
            checkForUser.close();
            connection.close();
        } catch (SQLException | QrGenerationException e) {
            throw new RuntimeException(e);
        }

    }

    public Database() {
        this(BackendServer.env.get("DB_URL"), BackendServer.env.get("DB_USERNAME", "db_team"), BackendServer.env.get("DB_PASSWORD", ""));
    }

    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    public void shutDownDatabase() {
        db.close();
    }

}
