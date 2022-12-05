package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.Constants;
import com.turtleshelldevelopment.utils.ResponseUtils;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateRecordEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        System.out.println("Update Record has been called");
        String lastName = request.params("lname");
        String firstName = request.params("fname");
        String middleName = request.params("mname");
        String sexIdentity = request.params("sex");
        if(lastName == null) {
            return ResponseUtils.createError("No Last Name", 400, response);
        }
        if(lastName.length() > 45) {
            return ResponseUtils.createError("Last Name too long (max 45 characters)", 400, response);
        }
        if(firstName.length() > 45) {
            return ResponseUtils.createError("First Name too long (max 45 characters)", 400, response);
        }
        if(middleName.length() > 45) {
            return ResponseUtils.createError("Middle Name too long (max 45 characters)", 400, response);
        }
        if(!Constants.CLIENT_SEX_IDENTITIES.contains(sexIdentity)) {
            return ResponseUtils.createError("Invalid Sex Identity", 400, response);
        }

        //TODO write stored procedure to take in these values and update them if need be (ignore values that are null/empty strings)


        return "";
    }
}
