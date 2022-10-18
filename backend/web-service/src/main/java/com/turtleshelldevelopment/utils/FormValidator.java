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
        return true;
    }

    public static LocalDate parseDateFromForm(String date) {
        try {
            return LocalDate.parse(date);
        } catch(DateTimeParseException e) {
            return null;
        }
    }



}
