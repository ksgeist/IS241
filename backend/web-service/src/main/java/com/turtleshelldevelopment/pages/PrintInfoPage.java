package com.turtleshelldevelopment;

import com.turtleshelldevelopment.utils.ModelUtil;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.*;

public class PrintInfo{

@Override
    public Object handle(Request request, Response response) {
        //
        String stringQuery = "SELECT * 
            FROM PatientInformation p, Vaccine v, Site s
            WHERE v.patient_id_vaccine = p.patient_id AND 
            s.counties_site = v.site_id
            p.last_ss_num = ? AND p.last_name = ?";
        if(request.queryParams("dob") != null){
            stringQuery+= "p.dob = ?";
        }
        PreparedStatement patientSearch = BackendServer.database.getConnection().prepareStatement(stringQuery+=";");

        patientSearch.setString(1, request.queryParams("ss4"));
        patientSearch.setString(2, request.queryParams("lname"));
        if(request.queryParams("dob" != null)){
            patientSearch.setString(3, request.queryParams("dob"));
        }

        ResultSet set = patientSearch.executeQuery();
       

    //columns and formatKey function are 
    //here only to output a quick/simple html
    //document to be printed
    String[] columns = new String[]
    {
       "patient_id",
       "first_name",
       "middle_name",
       "last_name",
       "last_ss_num",
       "dob",
       "email",

       "lot_num",
       "site_id",
       "patient_id",
       "administered_date",
       "manufactured",
       "dose",
       "administrated_by" 

       ,"site"
    };

    String formatKey(String str){
        String placeholder = str.substring(0, 1).toUpperCase + str.substring(1);
        for(int i = 0; i< placeholder.length(); i++){
            if(str[i] == "_"){
                str[i] = " ";
            }
        }
    }

    //Set Response.body, uses the columns and formatKey
    //because I was just trying to get something done quick
    //and that's what I had.
    response.body(
        () -> {
            String printOutput;
            for(int i = 0; i<15; i++)
            {
                //This is where the executed query gets its columns extracted
                //at "set.getString"
                //and subsequently it's turned into a rudimentary html layout
                printOutput+="<h1>"+formatKey(columns[i])+":"+"</h1>"
                +"<h2>"+set.getString(columns[i]) + "</h2>";
            }
        }
    );





    }
}


/*
    public Object handle(Request request, Response response) {
       @Override
        PreparedStatement patientSearch = BackendServer.database.getConnection().prepareStatement(
            "SELECT * 
            FROM PatientInformation p
            LEFT OUTER JOIN Vaccine v 
            ON v.patient_vaccine_id = p.patient_id
             WHERE"
            + request.queryParams("ss4") +" = p.last_ss_num AND"
            + request.queryParams("lname") +" = p.last_name;"
        );
        ResultSet set = patientSearch.executeQuery()
       }
*/
// BackendServer.db.getConnection
