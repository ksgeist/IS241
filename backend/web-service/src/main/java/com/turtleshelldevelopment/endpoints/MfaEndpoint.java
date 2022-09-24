package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.WebServer;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class MfaEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws ParseException {
        String jwtQuery = request.queryParams("jwt");
        String code = request.queryParams("code");
        DecodedJWT jwt = JWT.decode(jwtQuery);
        JSONObject jwtPayload = (JSONObject) new JSONParser().parse(jwt.getPayload());

        try {
            JSONObject success = new JSONObject();
            WebServer.JWT_ALGO.verify(jwt);

            String username = (String) jwtPayload.get("sub");
            TimeProvider timeProvider = new SystemTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA256);
            CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

            //Get Secret from Database
            PreparedStatement getSecret = WebServer.database.getConnection().prepareStatement("SELECT 2fa_secret FROM User WHERE username = ?;");
            getSecret.setString(1, username);
            ResultSet set;
            if(!(set = getSecret.executeQuery()).next()) {
                JSONObject error = new JSONObject();
                error.put("success", false);
                error.put("message", "Nonexistent user");
                response.status(401);
                return error;
            }
            String secret = set.getString("2fa_secret");

            if(verifier.isValidCode(secret, code)) {
                success.put("success", true);
                success.put("jwt", generateJWTToken(username));
            }

            return success;
        } catch (SignatureVerificationException | SQLException e) {
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
