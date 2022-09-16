package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.turtleshelldevelopment.WebServer;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Time;
import java.time.LocalTime;

public class LoginEndpoint implements Route {
    /***
     * Handles Login
     * Implementation requires a JSON String with username and password keys
     * example body: {"username": "XXXXX", "password": "XXXXXXX"}
     */
    @Override
    public Object handle(Request request, Response response) {
        String username, password;

        //Validate Authentication POST Request
        username = request.queryParams("username");
        password = request.queryParams("password");

        if(validate(username, password)) {
            System.out.println("user: " + username + ", password: " + password);
            JSONObject success = new JSONObject();
            success.put("jwt", generateJWTToken(username));
            response.status(200);
            return success;
        } else {
            response.status(400);
        }
        return "";
    }

    private boolean validate(String username, String password) {
        if(username == null || password == null || username.equals("") || password.equals("")) return false;
        return true;
    }

    private String generateJWTToken(String username) {
        return JWT.create()
                .withIssuer("covid-19-dash")
                .withSubject(username)
                .withNotBefore(Time.valueOf(LocalTime.now()))
                .sign(WebServer.JWT_ALGO);
    }
}
