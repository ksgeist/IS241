package com.turtleshelldevelopment.utils.db;

import com.turtleshelldevelopment.BackendServer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public record ProvidingUser(String firstName, String lastName, String email) {
    public static ProvidingUser getProvidingUser(int id) {
        try {
            Connection dbConn = BackendServer.database.getConnection();
            CallableStatement getInsurances = dbConn.prepareCall("CALL GET_PROVIDING_USER(?)");
            getInsurances.setInt(1, id);
            ResultSet res = getInsurances.executeQuery();
            if(res.next()) {
                ProvidingUser user = new ProvidingUser(res.getString("first_name"), res.getString("last_name"), res.getString("email"));
                getInsurances.close();
                dbConn.close();
                return user;
            } else {
                getInsurances.close();
                dbConn.close();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
