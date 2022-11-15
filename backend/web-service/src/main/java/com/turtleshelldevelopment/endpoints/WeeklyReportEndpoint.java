package com.turtleshelldevelopment.endpoints;

import spark.Request;
import spark.Response;
import spark.Route;

public class WeeklyReportEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        //TODO
        response.status(501);
        return null;
    }
}
