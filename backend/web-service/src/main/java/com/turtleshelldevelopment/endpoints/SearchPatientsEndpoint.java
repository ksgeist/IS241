package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.ModelUtil;
import com.turtleshelldevelopment.utils.Patient;
import com.turtleshelldevelopment.utils.ResponseUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchPatientsEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Map<String, Object> modelData = new ModelUtil().build();
        if(request.queryParams("ss4") == null || request.queryParams("lname") == null || request.queryParams("dob") == null) {
            return new VelocityTemplateEngine().render(new ModelAndView(modelData, "/frontend/search.vm"));
        }
        if(request.queryParams("ss4").isEmpty() && request.queryParams("lname").isEmpty() && request.queryParams("dob").isEmpty()) {
            return new VelocityTemplateEngine().render(new ModelAndView(modelData, "/frontend/search.vm"));
        }
        try(PreparedStatement patientSearch = BackendServer.database.getConnection().prepareCall("CALL SEARCH_PATIENTS(?, ?, ?)")) {
            System.out.println("applying params");
            if(!request.queryParams("ss4").isEmpty()) patientSearch.setString(1, request.queryParams("ss4"));
            else patientSearch.setNull(1, Types.VARCHAR);
            if(!request.queryParams("lname").isEmpty()) patientSearch.setNString(2, request.queryParams("lname"));
            //TODO finish up and fix date of birth field to database
            else patientSearch.setNull(2, Types.VARCHAR);
            System.out.println("Applying dob");
            patientSearch.setNull(3, Types.VARCHAR);
            //patientSearch.setDate(3, request.queryParams("dob"));
            System.out.println("Done!");
            ResultSet set = patientSearch.executeQuery();
            List<Patient> patientsList = new ArrayList<>();
            while(set.next()) {
                System.out.println("got a patient");
                patientsList.add(new Patient(set.getInt("patient_id"), set.getString("first_name"),
                        set.getString("middle_name"), set.getString("last_name"), set.getInt("last_ss_num"),
                        set.getDate("dob"), set.getString("email"), set.getString("gender")
                        )
                );
            }
            set.close();
            modelData.put("results", patientsList);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseUtils.createError("Failed to access database", 500, response);
        }
        System.out.println("Returning...");
        return new VelocityTemplateEngine().render(new ModelAndView(modelData, "/frontend/search.vm"));
    }
}
