package com.turtleshelldevelopment.endpoints;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginEndpoint implements Route {
    /***
     * Handles Login
     * Implementation requires a JSON String with username and password keys
     */
    @Override
    public Object handle(Request request, Response response) {
        JSONObject jsonBody;
        String username, password;
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
            success.put("jwt", generateJWTToken());
            response.body();
            response.status(200);
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
    }
}
