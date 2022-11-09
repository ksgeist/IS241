package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.FormValidator;
import com.turtleshelldevelopment.utils.ResponseUtils;
import com.turtleshelldevelopment.utils.UserType;
import com.turtleshelldevelopment.utils.db.Account;
import com.turtleshelldevelopment.utils.mfa.MultiFactorResponse;
import dev.samstevens.totp.exceptions.QrGenerationException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//*****************************************************
//*                                                   *
//* Created By: Colin Kinzel                          *
//* Created On: 10/6/2022, 4:28:35 PM                 *
//* Last Modified By: Colin Kinzel                    *
//* Last Modified On: 10/6/2022, 4:28:35 PM           *
//* Description: New Account Endpoint Handler Route   *
//*                                                   *
//*****************************************************
public class NewAccountEndpoint implements Route {

    @Override
    public Object handle(Request request, Response response) {
        BackendServer.serverLogger.debug("Handling New Account!");
        try {
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String firstName = request.queryParams("fname");
            String lastName = request.queryParams("lname");
            String email = request.queryParams("email");
            int site = Integer.parseInt(request.queryParams("site"));
            //Validate Username
            if (username.length() > 45) {
                //To long of username
                return ResponseUtils.createError("Username is too long.", 400, response);
            } else if (username.length() <= 4) {
                //To short of username
                return ResponseUtils.createError("Username is too short.", 400, response);
            }
            //Check for usernames with non letter or number
            if(!FormValidator.isValidUsername(username)) {
                return ResponseUtils.createError("Username contains invalid characters.", 400, response);
            }
            PreparedStatement statement = BackendServer.database.getConnection().prepareStatement("CALL CHECK_USERNAME(?);");
            statement.setString(1, username);
            if (statement.executeQuery().next()) {
                statement.close();
                return ResponseUtils.createError("Username already exists", 400, response);
            }

            Account acc = new Account(username, password);
            //Take in the password and hash it
            byte[] salt = acc.getPasswordSalt();
            //Put Permissions
            int userType = Integer.parseInt(request.queryParams("user_type"));
            UserType type;
            if ((type = UserType.isValidType(userType)) == null) {
                return ResponseUtils.createError("User Type is invalid.", 400, response);
            }
            try {
                //Insert new account
                CallableStatement insertUser = BackendServer.database.getConnection().prepareCall("CALL ADD_USER(?,?,?,?,?,?,?,?,?)");
                MultiFactorResponse mfa = acc.generateTOTPMultiFactor();
                insertUser.setString(1, username);
                insertUser.setBytes(2, acc.getPasswordHash());
                insertUser.setBytes(3, salt);
                insertUser.setString(4, mfa.secret());
                insertUser.setString(5, firstName);
                insertUser.setString(6, lastName);
                insertUser.setInt(7, site);
                insertUser.setInt(8, type.id());
                insertUser.setString(9, email);
                if (insertUser.executeUpdate() == 1) {
                    BackendServer.serverLogger.info("Success!");
                    return ResponseUtils.createCreateUserSuccessResponse(mfa.qr_code(), response).toString();
                } else {
                    insertUser.close();
                    BackendServer.serverLogger.error("Failed to update while creating new account");
                    return ResponseUtils.createError("Failed to update database", 500, response);
                }
            } catch (QrGenerationException e) {
                BackendServer.serverLogger.error("Error creating QR");
                return ResponseUtils.createError("Failed to create QR Code for 2fa", 500, response);
            }
        } catch (SQLException e) {
            BackendServer.serverLogger.info("ERROR: " + e.getMessage());
            return ResponseUtils.createError("Error handling creating a new account", 500, response);
        }
    }
}
