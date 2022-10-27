package com.turtleshelldevelopment.utils;

import org.json.JSONObject;

public class ResponseUtils {
    /**
     * Convenience method to create an error response for Routes
     * @param error Error Message to send to client
     * @return JSONObject to be given as route response body
     */
    public static JSONObject createError(String error) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("message", error);
        return errorResponse;
    }

    /**
     * Convenience method to create a success message for Routes for logging in
     * @param needsMfa if the client should
     * @return JSONObject to be given as route response body
     */
    public static JSONObject createLoginSuccess(boolean needsMfa) {
        JSONObject success = new JSONObject();
        success.put("request_2fa", needsMfa);
        return success;
    }
}
