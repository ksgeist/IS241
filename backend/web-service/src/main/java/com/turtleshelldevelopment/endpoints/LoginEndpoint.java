package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.WebServer;
import com.turtleshelldevelopment.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unchecked")
public class LoginEndpoint implements Route {

    /**
     * Login with username and password. Username and password must be in a well-formed
     * JSON body.
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return response body
     */
    @Override
    public Object handle(Request request, Response response) {
        String username, password;
        try {
            JSONObject body = (JSONObject) new JSONParser().parse(request.body());
            //Validate Authentication POST Request
            username = (String) body.get("username");
            password = (String) body.get("password");
            if(validate(username, password)) {
                JSONObject success = new JSONObject();
                success.put("request_2fa", true);
                generateMfaJWTToken(username, response);
                response.status(200);
                return success;
            } else {
                return ResponseUtils.createError("Invalid Username or Password");
            }
        } catch (SQLException e) {
            response.status(500);
            WebServer.serverLogger.warn(String.format("Error on handling login: %s", e.getMessage()));
            return ResponseUtils.createError("Server was unable to handle this request, Try again later.");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.status(401);
            return ResponseUtils.createError("Invalid Token");
        } catch (ParseException e) {
            response.status(400);
            return ResponseUtils.createError("Invalid request");
        }
    }


    /**
     * Validates the clients requested username and password
     * Verifying that none are missing or empty and that the
     * expected password is correct for the username provided
     * @param username The username as given by the client
     * @param password The password as given by the client
     * @return true if the client should be given a JWT token for the requested user, false if the
     * username, password is missing, empty, or incorrect
     * @throws SQLException If the Database request fails to respond
     * @throws NoSuchAlgorithmException If the cryptography algorithm does not exist
     * @throws InvalidKeySpecException If the security spec is invalid
     */
    private boolean validate(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(username == null || password == null || username.equals("") || password.equals("")) return false;
        //Get User From database if they exist
        CallableStatement getUser = WebServer.database.getConnection().prepareCall("CALL GET_USER(?)");
        getUser.setString(1, username);
        //Check for a valid user
        ResultSet rs;
        if((rs = getUser.executeQuery()).next()) {
            byte[] correct_password_hash = rs.getBytes("password_hash");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), rs.getBytes("salt"), 65536, 64 * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] password_hash = factory.generateSecret(spec).getEncoded();
            //WebServer.serverLogger.debug("Password Hash: " + Arrays.toString(password_hash));
            int diff = correct_password_hash.length ^ password_hash.length;
            for(int i = 0; i < correct_password_hash.length && i < password_hash.length; i++)
            {
                diff |= correct_password_hash[i] ^ password_hash[i];
            }
            //WebServer.serverLogger.debug("diff is " + diff);
            getUser.close();
            return diff == 0;
        } else {
            return false;
        }
    }

    /**
     * Creates a JWT Token specifically for continuing with a 2FA request
     * This must be given back to the server on request to /api/mfa as
     * the token.
     * This method will give append the token cookie directly to the response
     * object it is given.
     * @param username Username of the user this will be providing access to
     * @param response The response that will be given to the client once
     *                 completed.
     */
    private void generateMfaJWTToken(String username, Response response) {
        Instant time = Instant.now();
        Instant inst = time.plus(3, ChronoUnit.MINUTES);
        String jwt = JWT.create()
                .withIssuer(Issuers.MFA_LOGIN.getIssuer())
                .withSubject(username)
                .withClaim("mfa", true)
                .withNotBefore(time.minus(1, ChronoUnit.SECONDS))
                .withIssuedAt(time)
                .withExpiresAt(inst)
                .sign(WebServer.JWT_ALGO);
        response.cookie("/","token", jwt, 180, true, true);
    }
}
