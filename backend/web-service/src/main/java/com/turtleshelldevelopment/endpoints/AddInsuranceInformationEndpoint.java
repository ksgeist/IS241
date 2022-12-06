package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.SQLException;

public class AddInsuranceInformationEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {


        try(Connection conn = BackendServer.database.getDatabase().getConnection()) {

        } catch (SQLException e) {

        }
        return null;
    }
}
