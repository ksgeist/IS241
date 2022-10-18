package com.turtleshelldevelopment;

import org.json.simple.JSONArray;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Permissions {
    JSONArray permissions = new JSONArray();
    @SuppressWarnings("unchecked")
    public Permissions(String user) throws SQLException {
        CallableStatement getPermissions = WebServer.database.getConnection().prepareCall("CALL GET_PERMISSIONS(?)");
        getPermissions.setString(1, user);
        ResultSet set = getPermissions.executeQuery();
        if(set.next()) {
            if(set.getBoolean("read_patient")) permissions.add("READ_PATIENT");
            if(set.getBoolean("write_patient")) permissions.add("WRITE_PATIENT");
            if(set.getBoolean("edit_patient")) permissions.add("EDIT_PATIENT");
            if(set.getBoolean("reports")) permissions.add("REPORTS");
            if(set.getBoolean("add_user")) permissions.add("ADD_USER");
            if(set.getBoolean("edit_user")) permissions.add("EDIT_USER");
            if(set.getBoolean("add_site")) permissions.add("ADD_SITE");
            if(set.getBoolean("request_records")) permissions.add("REQUEST_RECORDS");
        }
        set.close();
        getPermissions.close();
    }

    public String[] getPermissionsAsString() {
        String[] perms = new String[permissions.size()];
        for(int i = 0; i < permissions.size();i++) {
            perms[i] = (String) permissions.get(i);
        }
        return perms;
    }

}
