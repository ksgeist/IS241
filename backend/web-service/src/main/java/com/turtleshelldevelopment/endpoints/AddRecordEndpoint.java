package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.Constants;
import com.turtleshelldevelopment.utils.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.*;
import java.util.regex.Pattern;

//*******************************************************************
//*                                                                 *
//* Created By: Entry Point Route                                   *
//* Created On: 10/14/2022, 1:17:25 PM                              *
//* Last Modified By: Colin Kinzel                                  *
//* Last Modified On: 10/14/2022, 1:17:25 PM                        *
//* Description: Handles Adding a Patient Entry into the database   *
//*                                                                 *
//*******************************************************************
public class AddRecordEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) {
        if(request.requestMethod().equalsIgnoreCase("POST")) {
            TokenUtils tokenVerifier = new TokenUtils(request.cookie("token"), Issuers.AUTHENTICATION.getIssuer());
            if(tokenVerifier.isInvalid()) {
                System.out.println("Token is invalid: " + tokenVerifier.getErrorReason());
                response.redirect("/");
                return ResponseUtils.createError("Invalid Token", 401, response);
            }

            System.out.println("Params are: " + request.queryParams());
            String date = request.queryParams("curr-date");
            String patientFirstName = request.queryParams("fname");
            String patientMiddleName = request.queryParams("mname");
            String patientLastName = request.queryParams("lname");
            String patientSSN = request.queryParams("ss4");
            String patientSex = request.queryParams("sex");
            try {
                Integer.parseInt(patientSSN);
            } catch (NumberFormatException e) {
                //TODO Return to form with previous fields and display error
                return ResponseUtils.createError("Invalid patient SSN", 400, response);
            }
            String patientBirthDate = request.queryParams("curr-date");

            //Patient Contact Info
            String contactAddress = request.queryParams("address");
            String contactPhone = request.queryParams("phone");
            String contactPhoneType = request.queryParams("PhoneType");
            String contactEmail = request.queryParams("email");

            String insuranceProviderName = request.queryParams("insProvider");
            String insuranceGroupNumber = request.queryParams("insGroup");
            String insurancePolicyNumber = request.queryParams("insPolicy");

            String vaccineManu = request.queryParams("vaxMan");
            String vaccineLotNum = request.queryParams("lotNumber");
            String vaccineSeries = request.queryParams("dose");
            int site, user;

            if(!FormValidator.checkValues()) {
                return ResponseUtils.createError("Missing Values", 400, response);
            }
            Pattern phoneNum = Pattern.compile("^\\([0-9]{3}\\) [0-9]{3} - [0-9]{4}$");
            if(!phoneNum.matcher(contactPhone).matches()) {
                return ResponseUtils.createError("Phone number not properly formatted", 400, response);
            } else {
                contactPhone = Pattern.compile("[0-9]+").matcher(contactPhone).group();
                System.out.println("phone is: " + contactPhone);
            }
            LocalDate dateFiled;
            if((dateFiled = FormValidator.parseDateFromForm(date)) == null) {
                return ResponseUtils.createError("Invalid Date Filed", 400, response);
            }
            LocalDate dateOfBirth;
            if((dateOfBirth = FormValidator.parseDateFromForm(patientBirthDate)) == null) {
                return ResponseUtils.createError("Invalid Date of Birth", 400, response);
            }
            if(!Constants.CLIENT_SEX_IDENTITIES.contains(patientSex)) {
                return ResponseUtils.createError("Invalid Sex Identity", 400, response);
            }
            if(Constants.VACCINE_SERIES.get(vaccineSeries) == null) {
                return ResponseUtils.createError("Invalid Vaccine Series", 400, response);
            }
            if((user = tokenVerifier.getUserId()) == -1) {
                return ResponseUtils.createError("Invalid User", 400, response);
            }
            if((site = tokenVerifier.getSiteId()) == -1) {
                return ResponseUtils.createError("Invalid Site", 400, response);
            }
            //TODO check if there are any similar patients in the database and notify of such
            try(Connection databaseConnection = BackendServer.database.getDatabase().getConnection();
                CallableStatement patientCall = databaseConnection.prepareCall("CALL ADD_PATIENT_INFO(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
            ) {
                patientCall.setString(1, patientFirstName);
                patientCall.setString(2, patientMiddleName);
                patientCall.setString(3, patientLastName);
                patientCall.setInt(4, Integer.parseInt(patientSSN));
                ZoneOffset timeZone = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());

                patientCall.setDate(5, new Date(dateOfBirth.toEpochSecond(LocalTime.now(), timeZone)));
                patientCall.setString(6, contactEmail);
                patientCall.setString(7, patientSex);
                //Not Required
                patientCall.setString(8, contactAddress);
                patientCall.setString(9, contactPhone);
                patientCall.setString(10, contactPhoneType);
                //Not Required
                patientCall.setString(11, insuranceProviderName);
                //Not Required
                patientCall.setString(12, insuranceGroupNumber);
                //Not Required
                patientCall.setString(13, insurancePolicyNumber);
                patientCall.setDate(14, new Date(dateFiled.toEpochSecond(LocalTime.now(), timeZone)));
                patientCall.setString(15, vaccineManu);
                patientCall.setInt(16, Constants.VACCINE_SERIES.get(vaccineSeries));
                patientCall.setInt(17, Integer.parseInt(vaccineLotNum));
                patientCall.setInt(18, user); //Administered by
                patientCall.setInt(19, site); //Site id
                if(patientCall.executeUpdate() == 1) {
                    System.out.println("Successfully wrote patient to database");
                }
                databaseConnection.close();
                return ResponseUtils.createSuccess("Added new patient", response);
            } catch (SQLException e) {
                e.printStackTrace();
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil(request).addError(500, "Failed to connect with database").build(), "/frontend/error.vm"));
            }
        }
        return ResponseUtils.createError("Failed to create patient", 500, response);
    }
}
