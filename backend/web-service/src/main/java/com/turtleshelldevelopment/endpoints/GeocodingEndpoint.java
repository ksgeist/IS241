package com.turtleshelldevelopment.endpoints;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeocodingEndpoint implements Route {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String GEOCODING_URL = "https://geocoding.geo.census.gov/geocoder/locations/onelineaddress?address=%s&benchmark=4&format=json";

    @Override
    public Object handle(Request request, Response response) throws IOException, InterruptedException, URISyntaxException {
        String addressLookup = request.queryParams("address");

        HttpRequest serviceRequest = HttpRequest.newBuilder().uri(new URI(String.format(GEOCODING_URL, addressLookup))).GET().build();
        HttpResponse<String> serviceResponse = client.send(serviceRequest, HttpResponse.BodyHandlers.ofString());
        response.body(serviceResponse.body());
        response.status(serviceResponse.statusCode());
        return "";
    }
}
