package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.FormValidator;
import com.turtleshelldevelopment.utils.ModelUtil;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.*;

//*******************************************************************
//*                                                                 *
//* Created By: Entry Point Route                                   *
//* Created On: 10/14/2022, 1:17:25 PM                              *
//* Last Modified By: Colin Kinzel                                  *
//* Last Modified On: 10/14/2022, 1:17:25 PM                        *
//* Description: Handles Adding a Patient Entry into the database   *
//*                                                                 *
//*******************************************************************
public class AddEntryEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) {
        if(request.requestMethod().equalsIgnoreCase("POST")) {
            System.out.println("Params are: " + request.queryParams());
            String date = request.queryParams("curr-date");
            String patientFirstName = request.queryParams("fname");
            String patientMiddleName = request.queryParams("mname");
            String patientLastName = request.queryParams("lname");
            String patientSSN = request.queryParams("ss4");
            try {
                Integer.parseInt(patientSSN);
            } catch (NumberFormatException e) {
                //TODO Return to form with previous fields and display error
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil()
                        .addError(500, "Failed to connect with database").build(),
                        "/frontend/error.vm"));
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

            String siteId = request.queryParams("siteid");

            if(!FormValidator.checkValues()) {
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().addError(500,
                        "Missing Values").build(), "/frontend/error.vm"));
            }
            LocalDate dateFiled;
            if((dateFiled = FormValidator.parseDateFromForm(date)) == null) {
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().addError(500,
                        "Invalid Date").build(), "/frontend/error.vm"));
            }
            LocalDate dateOfBirth;
            if((dateOfBirth = FormValidator.parseDateFromForm(patientBirthDate)) == null) {
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().addError(500,
                        "Invalid Date").build(), "/frontend/error.vm"));
            }
            //TODO fix this
            try(CallableStatement patientCall = BackendServer.database.getConnection().prepareCall("CALL ADD_PATIENT_INFO(?,?,?,?,?)")) {
                patientCall.setString(1, patientFirstName);
                patientCall.setString(2, patientMiddleName);
                patientCall.setString(3, patientLastName);
                patientCall.setInt(4, Integer.parseInt(patientSSN));
                ZoneOffset timeZone = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());

                patientCall.setDate(5, new Date(dateOfBirth.toEpochSecond(LocalTime.now(), timeZone)));
                patientCall.setString(6, contactEmail);
                patientCall.setString(7, contactAddress);
                patientCall.setString(8, contactPhone);
                patientCall.setString(9, contactPhoneType);
                patientCall.setString(10, insuranceProviderName);
                patientCall.setString(11, insuranceGroupNumber);
                patientCall.setString(12, insurancePolicyNumber);
                patientCall.setDate(13, new Date(dateFiled.toEpochSecond(LocalTime.now(), timeZone)));
                patientCall.setString(14, vaccineManu);
                patientCall.setString(15, vaccineLotNum);
                patientCall.setString(16, vaccineSeries);
                patientCall.setString(17, siteId);
                if(patientCall.executeUpdate() == 1) {
                    System.out.println("Successfully wrote patient to database");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return new VelocityTemplateEngine().render(new ModelAndView(new ModelUtil().addError(500, "Failed to connect with database").build(), "/frontend/error.vm"));
            }

            System.out.printf("Date: %s%n", date);

            return "";
        }
        return null;
    }
}
