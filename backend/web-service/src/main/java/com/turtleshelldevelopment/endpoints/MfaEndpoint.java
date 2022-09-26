package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.turtleshelldevelopment.Issuers;
import com.turtleshelldevelopment.WebServer;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.time.SystemTimeProvider;
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
        String jwtQuery = request.cookie("token");
        JSONObject bodyJSON = (JSONObject) new JSONParser().parse(request.body());
        if(!bodyJSON.containsKey("code")) {
            JSONObject failure = new JSONObject();
            failure.put("success", false);
            failure.put("message", "Missing code in Body");
            return failure;
        }
        String code = (String) bodyJSON.get("code");
        DecodedJWT jwt = JWT.decode(jwtQuery);
        WebServer.serverLogger.info("Payload is: " + jwt.getClaims().toString());
        try {
            JSONObject success = new JSONObject();
            WebServer.JWT_ALGO.verify(jwt);

            String username = jwt.getSubject();
            SystemTimeProvider timeProvider = new SystemTimeProvider();
            DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
            DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
            verifier.setAllowedTimePeriodDiscrepancy(2);

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

            WebServer.serverLogger.info("Secret is: " + secret);
            WebServer.serverLogger.info("Is Code (" + code + ") valid: " + verifier.isValidCode(secret, code));

            if(verifier.isValidCode(secret, code)) {
                success.put("success", true);
                response.cookie("token", generateJWTToken(username), 300, true, true);
                return success;
            } else {
                success.put("success", false);
                success.put("message", "Invalid Two Factor Code");
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
                .withIssuer(Issuers.AUTHENTICATION.getIssuer())
                .withSubject(username)
                .withClaim("mfa", true)
                .withNotBefore(Time.valueOf(LocalTime.now()))
                .sign(WebServer.JWT_ALGO);
    }
}
