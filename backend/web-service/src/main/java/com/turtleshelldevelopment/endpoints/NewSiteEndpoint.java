package com.turtleshelldevelopment.endpoints;

import spark.Request;
import spark.Response;
import spark.Route;

public class NewSiteEndpoint implements Route {
    @Override
    public Object handle(Request request, Response response) {

        String location = request.queryParams("location");
        String county = request.queryParams("county");
        String phoneNum = request.queryParams("phone");
        String fips = request.queryParams("fips");
        String zip = request.queryParams("zip");



        return null;
    }
}
