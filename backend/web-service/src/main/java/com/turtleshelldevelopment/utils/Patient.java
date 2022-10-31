package com.turtleshelldevelopment.utils;

import com.turtleshelldevelopment.BackendServer;

import java.sql.*;
import java.util.ArrayList;

public class Patient {

    private final int id;
    private final int SSNumber;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String gender;
    private final Date birthDate;

    public static ArrayList<Patient> searchForPatient() {
        ArrayList<Patient> patients = new ArrayList<>();
        try {
            Connection dbConn = BackendServer.database.getConnection();
            CallableStatement getPatients = dbConn.prepareCall("CALL SEARCH_PATIENTS(?, ?, ?)");

            ResultSet res = getPatients.executeQuery();
            while (res.next()) {
                new Patient(res.getInt("patient_id"), res.getString("first_name"),
                        res.getString("middle_name"), res.getString("last_name"),
                        res.getInt("last_ss_num"), res.getDate("dob"),
                        res.getString("email"), res.getString("gender"));
            }


            getPatients.close();
            dbConn.close();
        } catch (SQLException e) {
            return patients;
        }
        return patients;
    }


    public Patient(int id, String firstName, String middleName, String lastName, int SSNumber, Date birthDate, String email, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.SSNumber = SSNumber;
        this.birthDate = birthDate;
        this.email = email;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public int getSSNumber() {
        return SSNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }
}
