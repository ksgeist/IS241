package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.turtleshelldevelopment.WebServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
        JSONObject jsonBody;
        String username, password;

        //Validate Authentication POST Request
        try {
            jsonBody = (JSONObject) new JSONParser().parse(request.body());
            username = (String) jsonBody.get("username");
            password = (String) jsonBody.get("password");
        } catch(ParseException e) {
            JSONObject error = new JSONObject();
            error.put("error", "Malformed JSON");
            response.body(error.toJSONString());
            response.status(400);
            return "";
        }

        if(validate(username, password)) {
            System.out.println("read");
            System.out.println("user: " + username + ", password: " + password);
            JSONObject success = new JSONObject();
            success.put("jwt", generateJWTToken(username));
            System.out.println("Output is " + success.toJSONString());
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
