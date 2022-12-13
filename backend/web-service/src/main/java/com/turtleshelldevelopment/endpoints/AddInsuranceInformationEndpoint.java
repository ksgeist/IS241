package com.turtleshelldevelopment.endpoints;

import com.turtleshelldevelopment.BackendServer;
import com.turtleshelldevelopment.utils.ResponseUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddInsuranceInformationEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String provider = request.queryParams("insProvider");
        String groupNumber = request.queryParams("insGroup");
        String policyNumber = request.queryParams("insPolicy");
        String patient_id = request.params("id");

        try(Connection conn = BackendServer.database.getDatabase().getConnection();
            PreparedStatement add = conn.prepareStatement("INSERT INTO Insurance(patient_id, provider, group_number ,policy_number) VALUES (?,?,?,?)")) {
            add.setInt(1, Integer.parseInt(patient_id));
            add.setString(2, provider);
            add.setString(3, groupNumber);
            add.setString(4, policyNumber);
            add.executeUpdate();
            //response.redirect("/patient/view/" + patient_id);
            return ResponseUtils.createSuccess("Successfully added Insurance", response);
        } catch (SQLException e) {
            //response.redirect("/patient/view/" + patient_id);
            return ResponseUtils.createError("Failed to update Insurance", 500, response);
        }
    }
}
