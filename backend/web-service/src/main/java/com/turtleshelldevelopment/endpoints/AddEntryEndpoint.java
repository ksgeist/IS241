package com.turtleshelldevelopment.endpoints;

import spark.Request;
import spark.Response;
import spark.Route;
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

            String date = request.queryParams("inputDate");
            String patientFirstName = request.queryParams("fname");
            String patientMiddleName = request.queryParams("mname");
            String patientLastName = request.queryParams("lname");
            String patientSSN = request.queryParams("ss4");
            String patientBirthDate = request.queryParams("dob");

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

            return null;
        }
        return null;
    }
}
