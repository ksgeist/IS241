package com.turtleshelldevelopment.utils;

import org.json.JSONObject;
import spark.Response;

public class ResponseUtils {
    /**
     * Convenience method to create an error response for Routes
     * @param error Error Message to send to client
     * @param resp The response to be edited adding specific headers
     * @return JSONObject to be given as route response body
     */
    public static JSONObject createError(String error, int status, Response resp) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("message", error);
        resp.status(status);
        resp.header("Content-Type","application/api-problem+json");
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
