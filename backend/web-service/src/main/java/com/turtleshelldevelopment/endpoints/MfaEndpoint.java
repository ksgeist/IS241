package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.WebServer;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Time;
import java.time.LocalTime;

public class MfaEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) {
        String jwtQuery = request.queryParams("jwt");
        DecodedJWT jwt = JWT.decode(jwtQuery);

        try {
            JSONObject success = new JSONObject();
            WebServer.JWT_ALGO.verify(jwt);

            return success;
        } catch (SignatureVerificationException e) {
            JSONObject error = new JSONObject();
            error.put("success", false);
            error.put("message", "Invalid Signature");
            response.status(401);
            return error.toJSONString();
        }

    }

    private String generateJWTToken(String username) {
        return JWT.create()
                .withIssuer("covid-19-dash")
                .withSubject(username)
                .withClaim("mfa", true)
                .withNotBefore(Time.valueOf(LocalTime.now()))
                .sign(WebServer.JWT_ALGO);
    }
}
