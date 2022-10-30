package com.turtleshelldevelopment.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class FormValidator {
    public static boolean checkValues(String... params) {
        for (String param: params) {
            if(param == null || param.isEmpty()) {
                return false;
            }
        }
        System.out.println("Test");
        return true;
    }

    public static LocalDate parseDateFromForm(String date) {
        try {
            return LocalDate.parse(date);
        } catch(DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isValidUsername(String username) {
        if(username == null) return false;
        int nameLen = username.length();
        for(int i = 0; i < nameLen; i++) {
            if((!Character.isLetterOrDigit(username.charAt(i)))) {
                return false;
            }
        }
        return true;
    }



}
