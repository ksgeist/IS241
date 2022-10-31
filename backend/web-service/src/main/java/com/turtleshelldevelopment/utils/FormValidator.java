package com.turtleshelldevelopment.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class FormValidator {
    private static final Pattern phoneNumberPattern = Pattern.compile("^\\([0-9]{3}\\) [0-9]{3} - [0-9]{4}$");

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

    public static String parsePhoneNumberFromForm(String phoneNumber) {
        if(phoneNumberPattern.matcher(phoneNumber).find()) {
            String phoneNumberFormatted = phoneNumber.replaceAll("[ \\-()]", "");
            System.out.println("Phone number is now: " + phoneNumberFormatted);
            return phoneNumberFormatted;
        } else {
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
