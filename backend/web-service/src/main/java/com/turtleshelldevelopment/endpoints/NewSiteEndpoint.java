package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.*;
import java.util.regex.Pattern;

public class NewSiteEndpoint implements Route {
    private final Pattern streetAddressPattern = Pattern.compile("^\\d+(\\s\\w+)(\\s?\\w+)+$");


    //TODO Properly handle and make sure this works
    @Override
    public Object handle(Request request, Response response) throws SQLException {

        String location = request.queryParams("location");
        String county = request.queryParams("county");
        String phoneNum = request.queryParams("phone");
        String fips = request.queryParams("fips");
        String zip = request.queryParams("zip");

        Connection conn = BackendServer.database.getConnection();

        if(streetAddressPattern.matcher(location).find()) {
            CallableStatement createSite = conn.prepareCall("CALL ADD_SITE(?,?,?,?,?)");
            createSite.setString(1, location);
            createSite.setString(2, county);
            createSite.setString(3, phoneNum);
            createSite.setInt(4, Integer.parseInt(fips));
            createSite.setInt(5, Integer.parseInt(zip));
            createSite.registerOutParameter(6, JDBCType.INTEGER);
            if(createSite.executeUpdate() == 1) {
                JSONObject success = new JSONObject();
                success.put("error", 200);
                success.put("", "");
            } else {
                JSONObject err = new JSONObject();
                err.put("error", "500");
                err.put("message", "Could not add new site to the database!");
                response.body(err.toString());
            }
            createSite.close();

        }



        return "";
    }
}
