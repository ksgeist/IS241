package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.Account;
import com.turtleshelldevelopment.MultiFactorResponse;
import com.turtleshelldevelopment.WebServer;
import com.turtleshelldevelopment.utils.ModelUtil;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.json.simple.JSONObject;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

//*****************************************************
//*                                                   *
//* Created By: Colin Kinzel                          *
//* Created On: 10/6/2022, 4:28:35 PM                 *
//* Last Modified By: Colin Kinzel                    *
//* Last Modified On: 10/6/2022, 4:28:35 PM           *
//* Description: New Account Endpoint Handler Route   *
//*                                                   *
//*****************************************************
@SuppressWarnings("unchecked")
public class NewAccountEndpoint implements Route {

    @Override
    public Object handle(Request request, Response response) {
        WebServer.serverLogger.info("Handling New Account!");
        JSONObject body = new JSONObject();
        try {
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String firstName = request.queryParams("fname");
            String lastName = request.queryParams("lname");
            String email = request.queryParams("email");
            int site = Integer.parseInt(request.queryParams("site"));
            System.out.println("Username: " + username + ", password: " + password);

            //Validate Username
            PreparedStatement statement = WebServer.database.getConnection().prepareStatement("CALL CHECK_USERNAME(?);");
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                body.put("error", "Username already exists");
                return body;
            }

            Account acc = new Account(username, password);
            //WebServer.serverLogger.info("Username is Good");
            //Take in the password and hash it
            byte[] salt = acc.getPasswordSalt();
            //WebServer.serverLogger.info("Password is Good");
            //Put Permissions
            int userType = Integer.parseInt(request.queryParams("user_type"));
            //WebServer.serverLogger.info("Permissions is Good");
            try {
                //Insert new account
                CallableStatement insertUser = WebServer.database.getConnection().prepareCall("CALL ADD_USER(?,?,?,?,?,?,?,?,?)");
                MultiFactorResponse mfa = acc.generateTOTPMultiFactor();
                insertUser.setString(1, username);
                insertUser.setBytes(2, acc.getPasswordHash());
                insertUser.setBytes(3, salt);
                insertUser.setString(4, mfa.secret());
                insertUser.setString(5, firstName);
                insertUser.setString(6, lastName);
                insertUser.setInt(7, site);
                insertUser.setInt(8, userType);
                insertUser.setString(9, email);
            if (insertUser.executeUpdate() == 1) {
                body.put("error", "200");
                body.put("2fa", mfa.qr_code());
                System.out.println("User created: " + username + ", " + password + ", salt=" + Arrays.toString(salt));
                WebServer.serverLogger.info("Success!");
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().add("qr_code", body.get("2fa")).build(), "/frontend/success_create.vm"));
            } else {
                body.put("error", "500");
                body.put("message", "Failed to update database");
                WebServer.serverLogger.info("Failed to update");
            }
            insertUser.close();
            WebServer.serverLogger.info("Successfully put in database");
            } catch (QrGenerationException e) {
                WebServer.serverLogger.error("Error creating QR");
                body.put("error", "500");
                body.put("message", "Failed to Create QR Code for 2FA");
            }
            return body;
        } catch (SQLException e) {
            WebServer.serverLogger.info("ERROR: " + e.getMessage());
        }
        return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().add("qr_code", body.get("2fa")).build(), "/frontend/success_create.vm"));
    }

}
