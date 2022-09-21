package com.turtleshelldevelopment.endpoints;

import com.auth0.jwt.JWT;
import com.turtleshelldevelopment.WebServer;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.time.LocalTime;
import java.util.Arrays;

@SuppressWarnings("unchecked")
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

        try {
            if(validate(username, password)) {
                System.out.println("user: " + username + ", password: " + password);
                JSONObject success = new JSONObject();
                success.put("jwt", generateJWTToken(username));
                response.status(200);
                return success;
            } else {
                response.status(400);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.status(500);
            WebServer.serverLogger.warning(String.format("Error on handling login: %s", e.getMessage()));
        }
        return "";
    }

    private boolean validate(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(username == null || password == null || username.equals("") || password.equals("")) return false;
        CallableStatement getUser = WebServer.database.getConnection().prepareCall("CALL GET_USER(?)");
        getUser.setString(1, username);
        ResultSet rs;
        if((rs = getUser.executeQuery()).next()) {
            byte[] correct_password_hash = rs.getBytes("password_hash");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), rs.getBytes("salt"), 65536, 64 * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] password_hash = factory.generateSecret(spec).getEncoded();
            System.out.println("Password Hash: " + Arrays.toString(password_hash));
            int diff = correct_password_hash.length ^ password_hash.length;
            for(int i = 0; i < correct_password_hash.length && i < password_hash.length; i++)
            {
                diff |= correct_password_hash[i] ^ password_hash[i];
            }
            System.out.println("diff is " + diff);
            return diff == 0;
        }
        getUser.close();
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
