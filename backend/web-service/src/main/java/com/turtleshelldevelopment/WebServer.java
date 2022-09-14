package com.turtleshelldevelopment;

import com.turtleshelldevelopment.endpoints.LoginEndpoint;

import static spark.Service.ignite;
import static spark.Spark.*;

public class WebServer {

    public static void main(String[] args) {
        port(8080);
        path("/api", () -> post("/login", new LoginEndpoint()));
        ignite();
    }
}
