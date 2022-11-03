package com.turtleshelldevelopment;

import org.junit.*;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.*;

public class TestDashboard {
    static HttpClient client;
    private static final String serviceURL = "http://127.0.0.1:8091";


    @BeforeClass
    public static void setup() {
        CookieHandler.setDefault(new CookieManager());
        //System.setProperty("user.dir", Path.of(System.getProperty("user.dir")).getParent().toString());
        //Setup backend to Production
        BackendServer.main(new String[]{"-ePROD"});
        client = HttpClient.newBuilder()
                .cookieHandler(CookieHandler.getDefault())
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        //TODO remove user from database
        stop();
        awaitStop();
    }

    @DisplayName("Verify Redirect on Dashboard")
    @Test
    public void dashboardRedirectCheck() throws URISyntaxException, IOException, InterruptedException {
        assertNotNull(client);
        String dashboardUrl = "/dashboard";
        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + dashboardUrl)
        ).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());
    }

    @DisplayName("Verify Invalid JWT Token")
    @Test
    public void dashboardWithInvalidToken() throws URISyntaxException, IOException, InterruptedException {
        assertNotNull(client);
        String dashboardUrl = "/dashboard";

        ((CookieManager) client.cookieHandler().get()).getCookieStore().add(
                new URI(dashboardUrl),
                new HttpCookie(
                        "token",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
                ));
        HttpRequest request = HttpRequest.newBuilder().uri(
                new URI(serviceURL + dashboardUrl)
        ).POST(HttpRequest.BodyPublishers.ofString("")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());
    }

}
