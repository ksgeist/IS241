package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.Issuers;
import com.turtleshelldevelopment.utils.ResponseUtils;
import org.json.JSONException;
import org.json.JSONObject;
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


/**
 * Login Route to handle requests to /api/login
 */
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
            //parse body as JSON throw error if it cannot be parsed
            JSONObject body = new JSONObject(request.body());
            //get username and password from JSON body
            username = (String) body.get("username");
            password = (String) body.get("password");
            //Validate that the credentials are valid in our database
            if(validate(username, password)) {
                generateMfaJWTToken(username, response);
                //Set status to 200 OK
                response.status(200);
                //Return successful login response
                return ResponseUtils.createLoginSuccess(true);
            } else {
                //Invalid login, return error
                return ResponseUtils.createError("Invalid Username or Password");
            }
        } catch (SQLException e) {
            //Set status to 500 Internal Server Error
            response.status(500);
            //Log error
            BackendServer.serverLogger.warn("Error on handling login: {}", e.getMessage());
            //Return error due to SQL related error (most likely going to be an issue with the server being unresponsive)
            return ResponseUtils.createError("Server was unable to handle this request, Try again later.");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            //Set status to 401 Unauthorized
            response.status(500);
            //Log error
            BackendServer.serverLogger.error("Error on handling login: {}", e.getMessage());
            //Return error due to Issues related to the Java runtime environment
            return ResponseUtils.createError("Error with java runtime environment");
        } catch (JSONException e) {
            //Set status to 400 Bad Request
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
        //Check database if the user exists with Stored procedure
        CallableStatement getUser = BackendServer.database.getConnection().prepareCall("CALL GET_USER(?)");
        getUser.setString(1, username);
        ResultSet rs;
        //Execute Stored procedure call
        if((rs = getUser.executeQuery()).next()) {
            //Pull out the correct password hash from row (since we only store hashes in the database with salt)
            byte[] correctPasswordHash = rs.getBytes("password_hash");
            //Pull out salt from row
            byte[] salt = rs.getBytes("salt");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 64 * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            //Get the password hash of the password received from the client
            byte[] password_hash = factory.generateSecret(spec).getEncoded();
            //Get the difference and verify that the password is correct
            int diff = correctPasswordHash.length ^ password_hash.length;
            for(int i = 0; i < correctPasswordHash.length && i < password_hash.length; i++)
            {
                diff |= correctPasswordHash[i] ^ password_hash[i];
            }
            //Close callable statement
            getUser.close();
            //return if the difference is 0
            return diff == 0;
        } else {
            //If there is no rows return false
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
        //Get the time now
        Instant currentTime = Instant.now();
        //Add three minutes to current time, this being our expiration for the token
        Instant expiration = currentTime.plus(3, ChronoUnit.MINUTES);
        //Generate JWT token to be sent to client
        String jwt = JWT.create()
                .withIssuer(Issuers.MFA_LOGIN.getIssuer())
                .withSubject(username)
                .withClaim("mfa", true)
                .withNotBefore(currentTime.minus(1, ChronoUnit.SECONDS))
                .withIssuedAt(currentTime)
                .withExpiresAt(expiration)
                .sign(BackendServer.JWT_ALGO);
        //Set token cookie in response to client
        response.cookie("/","token", jwt, 180, true, true);
    }
}
